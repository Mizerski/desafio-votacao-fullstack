package com.mizerski.backend.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.mizerski.backend.dtos.request.CreateVoteRequest;
import com.mizerski.backend.dtos.response.PagedResponse;
import com.mizerski.backend.dtos.response.UserResponse;
import com.mizerski.backend.dtos.response.VoteResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.domains.Votes;
import com.mizerski.backend.models.entities.AgendaEntity;
import com.mizerski.backend.models.entities.UserEntity;
import com.mizerski.backend.models.entities.VoteEntity;
import com.mizerski.backend.models.enums.AgendaStatus;
import com.mizerski.backend.models.enums.VoteType;
import com.mizerski.backend.models.mappers.VoteMapper;
import com.mizerski.backend.repositories.AgendaRepository;
import com.mizerski.backend.repositories.VoteRepository;

/**
 * Testes unitários para o serviço de votos
 * 
 * Testa todos os métodos do VoteService incluindo cenários de sucesso e erro
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VoteService - Testes Unitários")
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private VoteMapper voteMapper;

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private ExceptionMappingService exceptionMappingService;

    @InjectMocks
    private VoteServiceImpl voteService;

    private CreateVoteRequest createVoteRequest;
    private VoteEntity voteEntity;
    private VoteResponse voteResponse;
    private Votes voteDomain;
    private AgendaEntity agendaEntityOpen;
    private AgendaEntity agendaEntityClosed;
    private UserEntity userEntity;
    private UserResponse userResponse;

    /**
     * Configuração inicial dos dados de teste
     */
    @BeforeEach
    void setUp() {
        // Dados de request
        createVoteRequest = new CreateVoteRequest();
        createVoteRequest.setVoteType(VoteType.YES);
        createVoteRequest.setAgendaId("agenda-123");
        createVoteRequest.setUserId("user-123");

        // Entidade de usuário
        userEntity = new UserEntity();
        userEntity.setId("user-123");
        userEntity.setName("João Silva");
        userEntity.setEmail("joao@email.com");

        // Response de usuário
        userResponse = UserResponse.builder()
                .id("user-123")
                .name("João Silva")
                .email("joao@email.com")
                .totalVotes(1)
                .build();

        // Entidade de agenda aberta
        agendaEntityOpen = new AgendaEntity();
        agendaEntityOpen.setId("agenda-123");
        agendaEntityOpen.setTitle("Pauta de Teste");
        agendaEntityOpen.setStatus(AgendaStatus.OPEN);

        // Entidade de agenda fechada
        agendaEntityClosed = new AgendaEntity();
        agendaEntityClosed.setId("agenda-456");
        agendaEntityClosed.setTitle("Pauta Fechada");
        agendaEntityClosed.setStatus(AgendaStatus.FINISHED);

        // Entidade de voto
        voteEntity = new VoteEntity();
        voteEntity.setId("vote-123");
        voteEntity.setVoteType(VoteType.YES);
        voteEntity.setUser(userEntity);
        voteEntity.setAgenda(agendaEntityOpen);
        voteEntity.setCreatedAt(LocalDateTime.now());
        voteEntity.setUpdatedAt(LocalDateTime.now());

        // Response de voto
        voteResponse = VoteResponse.builder()
                .id("vote-123")
                .voteType(VoteType.YES)
                .user(userResponse)
                .createdAt(LocalDateTime.now())
                .build();

        // Domínio de voto
        voteDomain = Votes.builder()
                .id("vote-123")
                .voteType(VoteType.YES)
                .build();
    }

    @Nested
    @DisplayName("Testes do método createVote")
    class CreateVoteTests {

        @Test
        @DisplayName("Deve criar voto com sucesso quando dados válidos")
        void deveCriarVotoComSucessoQuandoDadosValidos() {
            // Arrange
            when(agendaRepository.findById(createVoteRequest.getAgendaId())).thenReturn(Optional.of(agendaEntityOpen));
            when(voteRepository.findByUserIdAndAgendaId(createVoteRequest.getUserId(), createVoteRequest.getAgendaId()))
                    .thenReturn(Optional.empty());
            when(voteMapper.fromCreateRequest(createVoteRequest)).thenReturn(voteDomain);
            when(voteMapper.toEntity(voteDomain)).thenReturn(voteEntity);
            when(voteRepository.save(voteEntity)).thenReturn(voteEntity);
            when(voteMapper.toResponse(voteEntity)).thenReturn(voteResponse);

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(voteResponse, result.getValue().get());
            assertEquals(VoteType.YES, result.getValue().get().getVoteType());

            // Verificações de interação
            verify(agendaRepository).findById(createVoteRequest.getAgendaId());
            verify(voteRepository).findByUserIdAndAgendaId(createVoteRequest.getUserId(),
                    createVoteRequest.getAgendaId());
            verify(voteMapper).fromCreateRequest(createVoteRequest);
            verify(voteMapper).toEntity(voteDomain);
            verify(voteRepository).save(voteEntity);
            verify(voteMapper).toResponse(voteEntity);
        }

        @Test
        @DisplayName("Deve retornar erro quando agenda não existe")
        void deveRetornarErroQuandoAgendaNaoExiste() {
            // Arrange
            when(agendaRepository.findById(createVoteRequest.getAgendaId())).thenReturn(Optional.empty());

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("AGENDA_NOT_FOUND", result.getErrorCode().get());
            assertTrue(result.getErrorMessage().get().contains("Pauta não encontrada"));

            // Verificações de interação
            verify(agendaRepository).findById(createVoteRequest.getAgendaId());
            verify(voteRepository, never()).findByUserIdAndAgendaId(any(), any());
            verify(voteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando agenda não está aberta")
        void deveRetornarErroQuandoAgendaNaoEstaAberta() {
            // Arrange
            when(agendaRepository.findById(createVoteRequest.getAgendaId()))
                    .thenReturn(Optional.of(agendaEntityClosed));

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("AGENDA_NOT_OPEN", result.getErrorCode().get());
            assertTrue(result.getErrorMessage().get().contains("não está aberta"));

            // Verificações de interação
            verify(agendaRepository).findById(createVoteRequest.getAgendaId());
            verify(voteRepository, never()).findByUserIdAndAgendaId(any(), any());
            verify(voteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando usuário já votou")
        void deveRetornarErroQuandoUsuarioJaVotou() {
            // Arrange
            when(agendaRepository.findById(createVoteRequest.getAgendaId())).thenReturn(Optional.of(agendaEntityOpen));
            when(voteRepository.findByUserIdAndAgendaId(createVoteRequest.getUserId(), createVoteRequest.getAgendaId()))
                    .thenReturn(Optional.of(voteEntity));

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("USER_ALREADY_VOTED", result.getErrorCode().get());
            assertTrue(result.getErrorMessage().get().contains("já votou"));

            // Verificações de interação
            verify(agendaRepository).findById(createVoteRequest.getAgendaId());
            verify(voteRepository).findByUserIdAndAgendaId(createVoteRequest.getUserId(),
                    createVoteRequest.getAgendaId());
            verify(voteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando ocorre exceção durante criação")
        void deveRetornarErroQuandoOcorreExcecaoDuranteCriacao() {
            // Arrange
            when(agendaRepository.findById(createVoteRequest.getAgendaId())).thenReturn(Optional.of(agendaEntityOpen));
            when(voteRepository.findByUserIdAndAgendaId(createVoteRequest.getUserId(), createVoteRequest.getAgendaId()))
                    .thenReturn(Optional.empty());
            when(voteMapper.fromCreateRequest(createVoteRequest)).thenReturn(voteDomain);
            when(voteMapper.toEntity(voteDomain)).thenReturn(voteEntity);
            when(voteRepository.save(voteEntity)).thenThrow(new RuntimeException("Erro de banco de dados"));

            when(exceptionMappingService.mapExceptionToResult(any(Exception.class)))
                    .thenReturn(Result.error("DATABASE_ERROR", "Erro interno do servidor"));

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("DATABASE_ERROR", result.getErrorCode().get());

            // Verificações de interação
            verify(exceptionMappingService).mapExceptionToResult(any(Exception.class));
        }
    }

    @Test
    @DisplayName("Deve buscar voto por usuário e agenda com sucesso")
    void deveBuscarVotoPorUsuarioEAgendaComSucesso() {
        // Arrange
        when(voteRepository.findByUserIdAndAgendaId("user-123", "agenda-123"))
                .thenReturn(Optional.of(voteEntity));
        when(voteMapper.toResponse(voteEntity)).thenReturn(voteResponse);

        // Act
        Result<VoteResponse> result = voteService.getVoteByUserIdAndAgendaId("user-123", "agenda-123");

        // Assert
        assertTrue(result.isSuccess());
        assertTrue(result.getValue().isPresent());
        assertEquals(voteResponse, result.getValue().get());

        // Verificações de interação
        verify(voteRepository).findByUserIdAndAgendaId("user-123", "agenda-123");
        verify(voteMapper).toResponse(voteEntity);
    }

    @Test
    @DisplayName("Deve buscar todos os votos por agenda com sucesso")
    void deveBuscarTodosVotosPorAgendaComSucesso() {
        // Arrange
        List<VoteEntity> voteEntities = Arrays.asList(voteEntity);
        List<VoteResponse> voteResponses = Arrays.asList(voteResponse);

        when(voteRepository.findByAgendaId("agenda-123")).thenReturn(voteEntities);
        when(voteMapper.toResponse(voteEntity)).thenReturn(voteResponse);

        // Act
        Result<List<VoteResponse>> result = voteService.getAllVotesByAgendaId("agenda-123");

        // Assert
        assertTrue(result.isSuccess());
        assertTrue(result.getValue().isPresent());
        assertEquals(1, result.getValue().get().size());
        assertEquals(voteResponse, result.getValue().get().get(0));

        // Verificações de interação
        verify(voteRepository).findByAgendaId("agenda-123");
        verify(voteMapper).toResponse(voteEntity);
    }

    @Test
    @DisplayName("Deve buscar votos por agenda com paginação com sucesso")
    void deveBuscarVotosPorAgendaComPaginacaoComSucesso() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<VoteEntity> voteEntities = Arrays.asList(voteEntity);
        Page<VoteEntity> page = new PageImpl<>(voteEntities, pageable, 1);

        when(voteRepository.findByAgendaId("agenda-123", pageable)).thenReturn(page);
        when(voteMapper.toResponse(voteEntity)).thenReturn(voteResponse);

        // Act
        Result<PagedResponse<VoteResponse>> result = voteService.getAllVotesByAgendaId("agenda-123", pageable);

        // Assert
        assertTrue(result.isSuccess());
        assertTrue(result.getValue().isPresent());
        PagedResponse<VoteResponse> pagedResponse = result.getValue().get();
        assertEquals(1, pagedResponse.getContent().size());
        assertEquals(voteResponse, pagedResponse.getContent().get(0));
        assertEquals(0, pagedResponse.getPage());
        assertEquals(10, pagedResponse.getSize());
        assertEquals(1, pagedResponse.getTotalElements());

        // Verificações de interação
        verify(voteRepository).findByAgendaId("agenda-123", pageable);
        verify(voteMapper).toResponse(voteEntity);
    }
}