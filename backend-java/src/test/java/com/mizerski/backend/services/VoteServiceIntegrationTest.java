package com.mizerski.backend.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.mizerski.backend.dtos.request.CreateVoteRequest;
import com.mizerski.backend.dtos.response.VoteResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.entities.AgendaEntity;
import com.mizerski.backend.models.entities.UserEntity;
import com.mizerski.backend.models.entities.VoteEntity;
import com.mizerski.backend.models.enums.AgendaStatus;
import com.mizerski.backend.models.enums.VoteType;
import com.mizerski.backend.models.mappers.VoteMapper;
import com.mizerski.backend.repositories.AgendaRepository;
import com.mizerski.backend.repositories.UserRepository;
import com.mizerski.backend.repositories.VoteRepository;

/**
 * Testes de integração para VoteService com ExceptionMappingService e
 * ErrorMappingService
 * 
 * Demonstra como os serviços de mapeamento de erro funcionam em conjunto
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VoteService - Testes de Integração com Mapeamento de Erros")
class VoteServiceIntegrationTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private VoteMapper voteMapper;

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private UserRepository userRepository;

    private ExceptionMappingService exceptionMappingService;
    private VoteService voteService;

    private CreateVoteRequest createVoteRequest;
    private AgendaEntity agendaEntity;
    private UserEntity userEntity;
    private VoteEntity voteEntity;

    @BeforeEach
    void setUp() {
        // Inicializa o ExceptionMappingService real
        exceptionMappingService = new ExceptionMappingServiceImpl();

        // Inicializa o VoteService com dependências
        voteService = new VoteServiceImpl(
                voteRepository,
                voteMapper,
                agendaRepository,
                userRepository,
                exceptionMappingService);

        // Dados de teste
        createVoteRequest = new CreateVoteRequest();
        createVoteRequest.setVoteType(VoteType.YES);
        createVoteRequest.setAgendaId("agenda-123");
        createVoteRequest.setUserId("user-456");

        agendaEntity = new AgendaEntity();
        agendaEntity.setId("agenda-123");
        agendaEntity.setStatus(AgendaStatus.IN_PROGRESS);

        userEntity = new UserEntity();
        userEntity.setId("user-456");

        voteEntity = new VoteEntity();
        voteEntity.setId("vote-789");
        voteEntity.setVoteType(VoteType.YES);
    }

    @Nested
    @DisplayName("Testes de Mapeamento de Exceções")
    class ExceptionMappingTests {

        @Test
        @DisplayName("Deve mapear DataIntegrityViolationException para USER_ALREADY_VOTED")
        void deveMappearDataIntegrityViolationExceptionParaUserAlreadyVoted() {
            // Arrange
            when(agendaRepository.findById("agenda-123")).thenReturn(Optional.of(agendaEntity));
            when(voteRepository.findByUserIdAndAgendaId("user-456", "agenda-123"))
                    .thenReturn(Optional.empty());
            when(userRepository.findById("user-456")).thenReturn(Optional.of(userEntity));
            when(voteMapper.fromCreateRequest(any())).thenReturn(null);
            when(voteMapper.toEntity(any())).thenReturn(voteEntity);

            // Simula violação de constraint única (usuário já votou)
            DataIntegrityViolationException exception = new DataIntegrityViolationException(
                    "Duplicate entry for key 'votes_user_id_agenda_id_key'");
            when(voteRepository.save(any())).thenThrow(exception);

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert
            assertTrue(result.isError());
            assertEquals("USER_ALREADY_VOTED", result.getErrorCode().orElse(""));
            assertTrue(result.getErrorMessage().orElse("").contains("Duplicate entry"));
        }

        @Test
        @DisplayName("Deve mapear IllegalArgumentException para INVALID_DATA")
        void deveMappearIllegalArgumentExceptionParaInvalidData() {
            // Arrange
            when(agendaRepository.findById("agenda-123")).thenReturn(Optional.of(agendaEntity));
            when(voteRepository.findByUserIdAndAgendaId("user-456", "agenda-123"))
                    .thenReturn(Optional.empty());
            when(userRepository.findById("user-456")).thenReturn(Optional.of(userEntity));

            // Simula erro de validação
            IllegalArgumentException exception = new IllegalArgumentException("Tipo de voto inválido");
            when(voteMapper.fromCreateRequest(any())).thenThrow(exception);

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert
            assertTrue(result.isError());
            assertEquals("INVALID_DATA", result.getErrorCode().orElse(""));
            assertEquals("Tipo de voto inválido", result.getErrorMessage().orElse(""));
        }

        @Test
        @DisplayName("Deve mapear exceção desconhecida para UNKNOWN_ERROR")
        void deveMappearExcecaoDesconhecidaParaUnknownError() {
            // Arrange
            when(agendaRepository.findById("agenda-123")).thenReturn(Optional.of(agendaEntity));
            when(voteRepository.findByUserIdAndAgendaId("user-456", "agenda-123"))
                    .thenReturn(Optional.empty());
            when(userRepository.findById("user-456")).thenReturn(Optional.of(userEntity));

            // Simula exceção não mapeada
            RuntimeException exception = new RuntimeException("Erro inesperado do sistema");
            when(voteMapper.fromCreateRequest(any())).thenThrow(exception);

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert
            assertTrue(result.isError());
            assertEquals("UNKNOWN_ERROR", result.getErrorCode().orElse(""));
            assertEquals("Erro inesperado do sistema", result.getErrorMessage().orElse(""));
        }
    }

    @Nested
    @DisplayName("Testes de Validação de Negócio")
    class BusinessValidationTests {

        @Test
        @DisplayName("Deve retornar AGENDA_NOT_FOUND quando agenda não existe")
        void deveRetornarAgendaNotFoundQuandoAgendaNaoExiste() {
            // Arrange
            when(agendaRepository.findById("agenda-123")).thenReturn(Optional.empty());

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert
            assertTrue(result.isError());
            assertEquals("AGENDA_NOT_FOUND", result.getErrorCode().orElse(""));
            assertTrue(result.getErrorMessage().orElse("").contains("Pauta não encontrada"));
        }

        @Test
        @DisplayName("Deve retornar USER_ALREADY_VOTED quando usuário já votou")
        void deveRetornarUserAlreadyVotedQuandoUsuarioJaVotou() {
            // Arrange
            when(agendaRepository.findById("agenda-123")).thenReturn(Optional.of(agendaEntity));
            when(voteRepository.findByUserIdAndAgendaId("user-456", "agenda-123"))
                    .thenReturn(Optional.of(voteEntity));

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert
            assertTrue(result.isError());
            assertEquals("USER_ALREADY_VOTED", result.getErrorCode().orElse(""));
            assertTrue(result.getErrorMessage().orElse("").contains("já votou"));
        }

        @Test
        @DisplayName("Deve retornar AGENDA_NOT_OPEN quando agenda não está aberta")
        void deveRetornarAgendaNotOpenQuandoAgendaNaoEstaAberta() {
            // Arrange
            agendaEntity.setStatus(AgendaStatus.FINISHED);
            when(agendaRepository.findById("agenda-123")).thenReturn(Optional.of(agendaEntity));

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert
            assertTrue(result.isError());
            assertEquals("AGENDA_NOT_OPEN", result.getErrorCode().orElse(""));
            assertTrue(result.getErrorMessage().orElse("").contains("não está aberta"));
        }

        @Test
        @DisplayName("Deve retornar USER_NOT_FOUND quando usuário não existe")
        void deveRetornarUserNotFoundQuandoUsuarioNaoExiste() {
            // Arrange
            when(agendaRepository.findById("agenda-123")).thenReturn(Optional.of(agendaEntity));
            when(voteRepository.findByUserIdAndAgendaId("user-456", "agenda-123"))
                    .thenReturn(Optional.empty());
            when(userRepository.findById("user-456")).thenReturn(Optional.empty());

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert
            assertTrue(result.isError());
            assertEquals("USER_NOT_FOUND", result.getErrorCode().orElse(""));
            assertTrue(result.getErrorMessage().orElse("").contains("Usuário não encontrado"));
        }
    }

    @Nested
    @DisplayName("Testes de Integração Completa")
    class FullIntegrationTests {

        @Test
        @DisplayName("Deve demonstrar fluxo completo de tratamento de erros")
        void deveDemonstrarFluxoCompletoTratamentoErros() {
            // Este teste demonstra como os serviços trabalham em conjunto:
            // 1. VoteService detecta erro de negócio
            // 2. ExceptionMappingService mapeia exceções não previstas
            // 3. ErrorMappingService (no controller) mapeia Result.Error para HTTP

            // Arrange - simula cenário onde usuário já votou
            when(agendaRepository.findById("agenda-123")).thenReturn(Optional.of(agendaEntity));
            when(voteRepository.findByUserIdAndAgendaId("user-456", "agenda-123"))
                    .thenReturn(Optional.of(voteEntity));

            // Act
            Result<VoteResponse> result = voteService.createVote(createVoteRequest);

            // Assert - verifica que o erro foi detectado corretamente
            assertTrue(result.isError());
            assertEquals("USER_ALREADY_VOTED", result.getErrorCode().orElse(""));

            // O ErrorMappingService no controller mapearia este Result.Error para:
            // ResponseEntity.status(409).body(ErrorResponse)
        }
    }
}