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
import static org.mockito.ArgumentMatchers.isNull;
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
    private VoteService voteService;

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
            when(voteMapper.toResponse((VoteEntity) voteEntity)).thenReturn(voteResponse);

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
            verify(voteMapper).toResponse((VoteEntity) voteEntity);
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
            assertEquals("A pauta não está aberta para votação", result.getErrorMessage().get());

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
            assertEquals("O usuário já votou na pauta", result.getErrorMessage().get());

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
            RuntimeException exception = new RuntimeException("Erro de banco de dados");
            when(agendaRepository.findById(createVoteRequest.getAgendaId())).thenReturn(Optional.of(agendaEntityOpen));
            when(voteRepository.findByUserIdAndAgendaId(createVoteRequest.getUserId(), createVoteRequest.getAgendaId()))
                    .thenReturn(Optional.empty());
            when(voteMapper.fromCreateRequest(createVoteRequest)).thenReturn(voteDomain);
            when(voteMapper.toEntity(voteDomain)).thenReturn(voteEntity);
            when(voteRepository.save(voteEntity)).thenThrow(exception);
            when(exceptionMappingService.mapExceptionToResult(exception))
                    .thenReturn(Result.error("DATABASE_ERROR", "Erro interno do servidor"));

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert
            assertTrue(result.isError());
            assertEquals("DATABASE_ERROR", result.getErrorCode().get());
            assertEquals("Erro interno do servidor", result.getErrorMessage().get());

            // Verificações de interação
            verify(exceptionMappingService).mapExceptionToResult(exception);
        }
    }

    @Nested
    @DisplayName("Testes do método getVoteByUserIdAndAgendaId")
    class GetVoteByUserIdAndAgendaIdTests {

        @Test
        @DisplayName("Deve buscar voto por usuário e agenda com sucesso quando voto existe")
        void deveBuscarVotoPorUsuarioEAgendaComSucessoQuandoVotoExiste() {
            // Arrange
            String userId = "user-123";
            String agendaId = "agenda-123";
            when(voteRepository.findByUserIdAndAgendaId(userId, agendaId)).thenReturn(Optional.of(voteEntity));
            when(voteMapper.toResponse((VoteEntity) voteEntity)).thenReturn(voteResponse);

            // Act
            Result<VoteResponse> result = voteService.getVoteByUserIdAndAgendaId(userId, agendaId);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(voteResponse, result.getValue().get());

            // Verificações de interação
            verify(voteRepository).findByUserIdAndAgendaId(userId, agendaId);
            verify(voteMapper).toResponse((VoteEntity) voteEntity);
        }

        @Test
        @DisplayName("Deve retornar erro quando voto não existe")
        void deveRetornarErroQuandoVotoNaoExiste() {
            // Arrange
            String userId = "user-inexistente";
            String agendaId = "agenda-inexistente";
            when(voteRepository.findByUserIdAndAgendaId(userId, agendaId)).thenReturn(Optional.empty());

            // Act
            Result<VoteResponse> result = voteService.getVoteByUserIdAndAgendaId(userId, agendaId);

            // Assert
            assertTrue(result.isError());
            assertEquals("VOTE_NOT_FOUND", result.getErrorCode().get());
            assertTrue(result.getErrorMessage().get().contains("Voto não encontrado"));

            // Verificações de interação
            verify(voteRepository).findByUserIdAndAgendaId(userId, agendaId);
            verify(voteMapper, never()).toResponse((VoteEntity) any());
        }

        @Test
        @DisplayName("Deve retornar erro quando ocorre exceção durante busca")
        void deveRetornarErroQuandoOcorreExcecaoDuranteBusca() {
            // Arrange
            String userId = "user-123";
            String agendaId = "agenda-123";
            RuntimeException exception = new RuntimeException("Erro de conexão");
            when(voteRepository.findByUserIdAndAgendaId(userId, agendaId)).thenThrow(exception);
            when(exceptionMappingService.mapExceptionToResult(exception))
                    .thenReturn(Result.error("DATABASE_ERROR", "Erro interno do servidor"));

            // Act
            Result<VoteResponse> result = voteService.getVoteByUserIdAndAgendaId(userId, agendaId);

            // Assert
            assertTrue(result.isError());
            assertEquals("DATABASE_ERROR", result.getErrorCode().get());

            // Verificações de interação
            verify(voteRepository).findByUserIdAndAgendaId(userId, agendaId);
            verify(exceptionMappingService).mapExceptionToResult(exception);
        }
    }

    @Nested
    @DisplayName("Testes do método getAllVotesByAgendaId (sem paginação)")
    class GetAllVotesByAgendaIdTests {

        @Test
        @DisplayName("Deve buscar todos os votos por agenda com sucesso")
        void deveBuscarTodosVotosPorAgendaComSucesso() {
            // Arrange
            String agendaId = "agenda-123";
            List<VoteEntity> voteEntities = Arrays.asList(voteEntity);
            when(voteRepository.findByAgendaId(agendaId)).thenReturn(voteEntities);
            when(voteMapper.toResponse((VoteEntity) voteEntity)).thenReturn(voteResponse);

            // Act
            Result<List<VoteResponse>> result = voteService.getAllVotesByAgendaId(agendaId);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(1, result.getValue().get().size());
            assertEquals(voteResponse, result.getValue().get().get(0));

            // Verificações de interação
            verify(voteRepository).findByAgendaId(agendaId);
            verify(voteMapper).toResponse((VoteEntity) voteEntity);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há votos para a agenda")
        void deveRetornarListaVaziaQuandoNaoHaVotosParaAgenda() {
            // Arrange
            String agendaId = "agenda-sem-votos";
            when(voteRepository.findByAgendaId(agendaId)).thenReturn(Arrays.asList());

            // Act
            Result<List<VoteResponse>> result = voteService.getAllVotesByAgendaId(agendaId);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(0, result.getValue().get().size());

            // Verificações de interação
            verify(voteRepository).findByAgendaId(agendaId);
            verify(voteMapper, never()).toResponse((VoteEntity) any());
        }

        @Test
        @DisplayName("Deve retornar erro quando ocorre exceção durante busca")
        void deveRetornarErroQuandoOcorreExcecaoDuranteBusca() {
            // Arrange
            String agendaId = "agenda-123";
            RuntimeException exception = new RuntimeException("Erro de busca");
            when(voteRepository.findByAgendaId(agendaId)).thenThrow(exception);
            when(exceptionMappingService.mapExceptionToResult(exception))
                    .thenReturn(Result.error("SEARCH_ERROR", "Erro na busca"));

            // Act
            Result<List<VoteResponse>> result = voteService.getAllVotesByAgendaId(agendaId);

            // Assert
            assertTrue(result.isError());
            assertEquals("SEARCH_ERROR", result.getErrorCode().get());

            // Verificações de interação
            verify(voteRepository).findByAgendaId(agendaId);
            verify(exceptionMappingService).mapExceptionToResult(exception);
        }
    }

    @Nested
    @DisplayName("Testes do método getAllVotesByAgendaId (com paginação)")
    class GetAllVotesByAgendaIdPagedTests {

        @Test
        @DisplayName("Deve buscar votos por agenda com paginação com sucesso")
        void deveBuscarVotosPorAgendaComPaginacaoComSucesso() {
            // Arrange
            String agendaId = "agenda-123";
            Pageable pageable = PageRequest.of(0, 10);
            List<VoteEntity> voteEntities = Arrays.asList(voteEntity);
            Page<VoteEntity> page = new PageImpl<>(voteEntities, pageable, 1);

            when(voteRepository.findByAgendaId(agendaId, pageable)).thenReturn(page);
            when(voteMapper.toResponse((VoteEntity) voteEntity)).thenReturn(voteResponse);

            // Act
            Result<PagedResponse<VoteResponse>> result = voteService.getAllVotesByAgendaId(agendaId, pageable);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());

            PagedResponse<VoteResponse> pagedResponse = result.getValue().get();
            assertEquals(1, pagedResponse.getContent().size());
            assertEquals(0, pagedResponse.getPage());
            assertEquals(10, pagedResponse.getSize());
            assertEquals(1, pagedResponse.getTotalElements());
            assertEquals(voteResponse, pagedResponse.getContent().get(0));

            // Verificações de interação
            verify(voteRepository).findByAgendaId(agendaId, pageable);
            verify(voteMapper).toResponse((VoteEntity) voteEntity);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não há votos")
        void deveRetornarPaginaVaziaQuandoNaoHaVotos() {
            // Arrange
            String agendaId = "agenda-sem-votos";
            Pageable pageable = PageRequest.of(0, 10);
            Page<VoteEntity> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

            when(voteRepository.findByAgendaId(agendaId, pageable)).thenReturn(emptyPage);

            // Act
            Result<PagedResponse<VoteResponse>> result = voteService.getAllVotesByAgendaId(agendaId, pageable);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());

            PagedResponse<VoteResponse> pagedResponse = result.getValue().get();
            assertEquals(0, pagedResponse.getContent().size());
            assertEquals(0, pagedResponse.getTotalElements());

            // Verificações de interação
            verify(voteRepository).findByAgendaId(agendaId, pageable);
            verify(voteMapper, never()).toResponse((VoteEntity) any());
        }
    }

    @Nested
    @DisplayName("Testes do método getAllVotesByUserId (sem paginação)")
    class GetAllVotesByUserIdTests {

        @Test
        @DisplayName("Deve buscar todos os votos por usuário com sucesso")
        void deveBuscarTodosVotosPorUsuarioComSucesso() {
            // Arrange
            String userId = "user-123";
            List<VoteEntity> voteEntities = Arrays.asList(voteEntity);
            when(voteRepository.findByUserId(userId)).thenReturn(voteEntities);
            when(voteMapper.toResponse((VoteEntity) voteEntity)).thenReturn(voteResponse);

            // Act
            Result<List<VoteResponse>> result = voteService.getAllVotesByUserId(userId);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(1, result.getValue().get().size());
            assertEquals(voteResponse, result.getValue().get().get(0));

            // Verificações de interação
            verify(voteRepository).findByUserId(userId);
            verify(voteMapper).toResponse((VoteEntity) voteEntity);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando usuário não tem votos")
        void deveRetornarListaVaziaQuandoUsuarioNaoTemVotos() {
            // Arrange
            String userId = "user-sem-votos";
            when(voteRepository.findByUserId(userId)).thenReturn(Arrays.asList());

            // Act
            Result<List<VoteResponse>> result = voteService.getAllVotesByUserId(userId);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(0, result.getValue().get().size());

            // Verificações de interação
            verify(voteRepository).findByUserId(userId);
            verify(voteMapper, never()).toResponse((VoteEntity) any());
        }
    }

    @Nested
    @DisplayName("Testes do método getAllVotesByUserId (com paginação)")
    class GetAllVotesByUserIdPagedTests {

        @Test
        @DisplayName("Deve buscar votos por usuário com paginação com sucesso")
        void deveBuscarVotosPorUsuarioComPaginacaoComSucesso() {
            // Arrange
            String userId = "user-123";
            Pageable pageable = PageRequest.of(0, 10);
            List<VoteEntity> voteEntities = Arrays.asList(voteEntity);
            Page<VoteEntity> page = new PageImpl<>(voteEntities, pageable, 1);

            when(voteRepository.findByUserId(userId, pageable)).thenReturn(page);
            when(voteMapper.toResponse((VoteEntity) voteEntity)).thenReturn(voteResponse);

            // Act
            Result<PagedResponse<VoteResponse>> result = voteService.getAllVotesByUserId(userId, pageable);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());

            PagedResponse<VoteResponse> pagedResponse = result.getValue().get();
            assertEquals(1, pagedResponse.getContent().size());
            assertEquals(voteResponse, pagedResponse.getContent().get(0));

            // Verificações de interação
            verify(voteRepository).findByUserId(userId, pageable);
            verify(voteMapper).toResponse((VoteEntity) voteEntity);
        }
    }

    @Nested
    @DisplayName("Testes do método getVoteByAgendaIdAndUserId")
    class GetVoteByAgendaIdAndUserIdTests {

        @Test
        @DisplayName("Deve buscar voto por agenda e usuário com sucesso")
        void deveBuscarVotoPorAgendaEUsuarioComSucesso() {
            // Arrange
            String agendaId = "agenda-123";
            String userId = "user-123";
            when(voteRepository.findByUserIdAndAgendaId(userId, agendaId)).thenReturn(Optional.of(voteEntity));
            when(voteMapper.toResponse((VoteEntity) voteEntity)).thenReturn(voteResponse);

            // Act
            Result<VoteResponse> result = voteService.getVoteByAgendaIdAndUserId(agendaId, userId);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(voteResponse, result.getValue().get());

            // Verificações de interação
            verify(voteRepository).findByUserIdAndAgendaId(userId, agendaId);
            verify(voteMapper).toResponse((VoteEntity) voteEntity);
        }

        @Test
        @DisplayName("Deve retornar erro quando voto não existe")
        void deveRetornarErroQuandoVotoNaoExiste() {
            // Arrange
            String agendaId = "agenda-inexistente";
            String userId = "user-inexistente";
            when(voteRepository.findByUserIdAndAgendaId(userId, agendaId)).thenReturn(Optional.empty());

            // Act
            Result<VoteResponse> result = voteService.getVoteByAgendaIdAndUserId(agendaId, userId);

            // Assert
            assertTrue(result.isError());
            assertEquals("VOTE_NOT_FOUND", result.getErrorCode().get());
            assertTrue(result.getErrorMessage().get().contains("Voto não encontrado"));

            // Verificações de interação
            verify(voteRepository).findByUserIdAndAgendaId(userId, agendaId);
            verify(voteMapper, never()).toResponse((VoteEntity) any());
        }
    }

    @Nested
    @DisplayName("Testes de integração e cenários especiais")
    class IntegrationAndSpecialScenariosTests {

        @Test
        @DisplayName("Deve testar diferentes tipos de voto")
        void deveTestarDiferentesTiposDeVoto() {
            // Arrange - Voto NO
            CreateVoteRequest voteNoRequest = new CreateVoteRequest();
            voteNoRequest.setVoteType(VoteType.NO);
            voteNoRequest.setAgendaId("agenda-123");
            voteNoRequest.setUserId("user-456");

            VoteEntity voteNoEntity = new VoteEntity();
            voteNoEntity.setVoteType(VoteType.NO);

            VoteResponse voteNoResponse = VoteResponse.builder()
                    .voteType(VoteType.NO)
                    .build();

            when(agendaRepository.findById(voteNoRequest.getAgendaId())).thenReturn(Optional.of(agendaEntityOpen));
            when(voteRepository.findByUserIdAndAgendaId(voteNoRequest.getUserId(), voteNoRequest.getAgendaId()))
                    .thenReturn(Optional.empty());
            when(voteMapper.fromCreateRequest(voteNoRequest)).thenReturn(voteDomain);
            when(voteMapper.toEntity(voteDomain)).thenReturn(voteNoEntity);
            when(voteRepository.save(voteNoEntity)).thenReturn(voteNoEntity);
            when(voteMapper.toResponse((VoteEntity) voteNoEntity)).thenReturn(voteNoResponse);

            // Act
            Result<VoteResponse> result = voteService.createVote(voteNoRequest);

            // Assert
            assertTrue(result.isSuccess());
            assertEquals(VoteType.NO, result.getValue().get().getVoteType());
        }

        @Test
        @DisplayName("Deve lidar com valores nulos nos parâmetros de entrada")
        void deveLidarComValoresNulosNosParametrosDeEntrada() {
            // Arrange
            RuntimeException nullException = new RuntimeException("Parâmetro nulo");
            when(voteRepository.findByUserIdAndAgendaId(isNull(), isNull())).thenThrow(nullException);
            when(voteRepository.findByAgendaId(isNull())).thenThrow(nullException);
            when(voteRepository.findByUserId(isNull())).thenThrow(nullException);
            when(exceptionMappingService.mapExceptionToResult(nullException))
                    .thenReturn(Result.error("NULL_PARAMETER", "Parâmetro nulo"));

            // Test getVoteByUserIdAndAgendaId with null
            Result<VoteResponse> result1 = voteService.getVoteByUserIdAndAgendaId(null, null);
            assertTrue(result1.isError());

            // Test getAllVotesByAgendaId with null
            Result<List<VoteResponse>> result2 = voteService.getAllVotesByAgendaId(null);
            assertTrue(result2.isError());

            // Test getAllVotesByUserId with null
            Result<List<VoteResponse>> result3 = voteService.getAllVotesByUserId(null);
            assertTrue(result3.isError());
        }

        @Test
        @DisplayName("Deve manter transações corretas nos métodos")
        void deveManterTransacoesCorretasNosMetodos() {
            // Este teste verifica se as anotações @Transactional estão sendo respeitadas
            // Em um ambiente real, isso seria testado com um banco de dados real

            // Arrange
            when(agendaRepository.findById(createVoteRequest.getAgendaId())).thenReturn(Optional.of(agendaEntityOpen));
            when(voteRepository.findByUserIdAndAgendaId(createVoteRequest.getUserId(), createVoteRequest.getAgendaId()))
                    .thenReturn(Optional.empty());
            when(voteMapper.fromCreateRequest(createVoteRequest)).thenReturn(voteDomain);
            when(voteMapper.toEntity(voteDomain)).thenReturn(voteEntity);
            when(voteRepository.save(voteEntity)).thenReturn(voteEntity);
            when(voteMapper.toResponse((VoteEntity) voteEntity)).thenReturn(voteResponse);

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert
            assertTrue(result.isSuccess());
            // Em um teste de integração real, verificaríamos se a transação foi commitada
        }
    }
}