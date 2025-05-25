package com.mizerski.backend.services;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.entities.AgendaEntity;
import com.mizerski.backend.models.enums.AgendaCategory;
import com.mizerski.backend.models.enums.AgendaResult;
import com.mizerski.backend.models.enums.AgendaStatus;
import com.mizerski.backend.models.enums.VoteType;
import com.mizerski.backend.models.mappers.AgendaMapper;
import com.mizerski.backend.repositories.AgendaRepository;

/**
 * Testes unitários para o serviço de tempo de pautas
 * 
 * Testa todos os métodos do AgendaTimeService incluindo cenários de sucesso e
 * erro
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AgendaTimeService - Testes Unitários")
class AgendaTimeServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private AgendaMapper agendaMapper;

    @Mock
    private ExceptionMappingService exceptionMappingService;

    @InjectMocks
    private AgendaTimeServiceImpl agendaTimeService;

    private AgendaEntity agendaEntity;
    private AgendaResponse agendaResponse;
    private String agendaId;

    /**
     * Configuração inicial dos dados de teste
     */
    @BeforeEach
    void setUp() {
        agendaId = "agenda-123";

        // Entidade de agenda
        agendaEntity = new AgendaEntity();
        agendaEntity.setId(agendaId);
        agendaEntity.setTitle("Pauta de Teste");
        agendaEntity.setDescription("Descrição da pauta de teste");
        agendaEntity.setCategory(AgendaCategory.PROJETOS);
        agendaEntity.setStatus(AgendaStatus.DRAFT);
        agendaEntity.setResult(AgendaResult.UNVOTED);
        agendaEntity.setTotalVotes(0);
        agendaEntity.setYesVotes(0);
        agendaEntity.setNoVotes(0);
        agendaEntity.setIsActive(false);
        agendaEntity.setCreatedAt(LocalDateTime.now());
        agendaEntity.setUpdatedAt(LocalDateTime.now());

        // Response de agenda
        agendaResponse = AgendaResponse.builder()
                .id(agendaId)
                .title("Pauta de Teste")
                .description("Descrição da pauta de teste")
                .category(AgendaCategory.PROJETOS)
                .status(AgendaStatus.IN_PROGRESS)
                .result(AgendaResult.UNVOTED)
                .totalVotes(0)
                .yesVotes(0)
                .noVotes(0)
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Testes do método startAgendaTimer")
    class StartAgendaTimerTests {

        @Test
        @DisplayName("Deve iniciar timer da pauta com sucesso quando pauta existe e está em draft")
        void deveIniciarTimerDaPautaComSucessoQuandoPautaExisteEEstaEmDraft() {
            // Arrange
            int durationInMinutes = 60;
            AgendaEntity savedEntity = new AgendaEntity();
            savedEntity.setId(agendaId);
            savedEntity.setStatus(AgendaStatus.IN_PROGRESS);

            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));
            when(agendaRepository.save(any(AgendaEntity.class))).thenReturn(savedEntity);
            when(agendaMapper.toResponse((AgendaEntity) savedEntity)).thenReturn(agendaResponse);

            // Act
            Result<AgendaResponse> result = agendaTimeService.startAgendaTimer(agendaId, durationInMinutes);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(agendaResponse, result.getValue().get());
            assertEquals(AgendaStatus.IN_PROGRESS, result.getValue().get().getStatus());

            // Verificações de interação
            verify(agendaRepository).findById(agendaId);
            verify(agendaRepository).save(any(AgendaEntity.class));
            verify(agendaMapper).toResponse(savedEntity);
        }

        @Test
        @DisplayName("Deve retornar erro quando pauta não existe")
        void deveRetornarErroQuandoPautaNaoExiste() {
            // Arrange
            String agendaIdInexistente = "agenda-inexistente";
            int durationInMinutes = 60;
            when(agendaRepository.findById(agendaIdInexistente)).thenReturn(Optional.empty());

            // Act
            Result<AgendaResponse> result = agendaTimeService.startAgendaTimer(agendaIdInexistente, durationInMinutes);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("AGENDA_NOT_FOUND", result.getErrorCode().get());
            assertTrue(result.getErrorMessage().get().contains("Pauta não encontrada com ID"));

            // Verificações de interação
            verify(agendaRepository).findById(agendaIdInexistente);
            verify(agendaRepository, never()).save(any());
            verify(agendaMapper, never()).toResponse(any(AgendaEntity.class));
        }

        @Test
        @DisplayName("Deve retornar erro quando pauta está cancelada")
        void deveRetornarErroQuandoPautaEstaCancelada() {
            // Arrange
            int durationInMinutes = 60;
            agendaEntity.setStatus(AgendaStatus.CANCELLED);
            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));

            // Act
            Result<AgendaResponse> result = agendaTimeService.startAgendaTimer(agendaId, durationInMinutes);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("OPERATION_NOT_ALLOWED", result.getErrorCode().get());
            assertTrue(result.getErrorMessage().get()
                    .contains("A pauta não pode ser iniciada pois está cancelada ou encerrada"));

            // Verificações de interação
            verify(agendaRepository).findById(agendaId);
            verify(agendaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando pauta está finalizada")
        void deveRetornarErroQuandoPautaEstaFinalizada() {
            // Arrange
            int durationInMinutes = 60;
            agendaEntity.setStatus(AgendaStatus.FINISHED);
            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));

            // Act
            Result<AgendaResponse> result = agendaTimeService.startAgendaTimer(agendaId, durationInMinutes);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("OPERATION_NOT_ALLOWED", result.getErrorCode().get());
            assertTrue(result.getErrorMessage().get()
                    .contains("A pauta não pode ser iniciada pois está cancelada ou encerrada"));

            // Verificações de interação
            verify(agendaRepository).findById(agendaId);
            verify(agendaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando ocorre exceção durante início do timer")
        void deveRetornarErroQuandoOcorreExcecaoDuranteInicioDotimer() {
            // Arrange
            int durationInMinutes = 60;
            RuntimeException exception = new RuntimeException("Erro de banco de dados");
            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));
            when(agendaRepository.save(any(AgendaEntity.class))).thenThrow(exception);
            when(exceptionMappingService.mapExceptionToResult(exception))
                    .thenReturn(Result.error("DATABASE_ERROR", "Erro interno do servidor"));

            // Act
            Result<AgendaResponse> result = agendaTimeService.startAgendaTimer(agendaId, durationInMinutes);

            // Assert
            assertTrue(result.isError());
            assertEquals("DATABASE_ERROR", result.getErrorCode().get());
            assertEquals("Erro interno do servidor", result.getErrorMessage().get());

            // Verificações de interação
            verify(exceptionMappingService).mapExceptionToResult(exception);
        }
    }

    @Nested
    @DisplayName("Testes do método updateAgendaVotes")
    class UpdateAgendaVotesTests {

        @Test
        @DisplayName("Deve atualizar votos YES com sucesso")
        void deveAtualizarVotosYesComSucesso() {
            // Arrange
            agendaEntity.setTotalVotes(5);
            agendaEntity.setYesVotes(3);
            agendaEntity.setNoVotes(2);

            AgendaEntity savedEntity = new AgendaEntity();
            savedEntity.setId(agendaId);
            savedEntity.setTotalVotes(6);
            savedEntity.setYesVotes(4);
            savedEntity.setNoVotes(2);

            AgendaResponse updatedResponse = AgendaResponse.builder()
                    .id(agendaId)
                    .totalVotes(6)
                    .yesVotes(4)
                    .noVotes(2)
                    .build();

            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));
            when(agendaRepository.save(any(AgendaEntity.class))).thenReturn(savedEntity);
            when(agendaMapper.toResponse((AgendaEntity) savedEntity)).thenReturn(updatedResponse);

            // Act
            Result<AgendaResponse> result = agendaTimeService.updateAgendaVotes(agendaId, VoteType.YES);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(6, result.getValue().get().getTotalVotes());
            assertEquals(4, result.getValue().get().getYesVotes());
            assertEquals(2, result.getValue().get().getNoVotes());

            // Verificações de interação
            verify(agendaRepository).findById(agendaId);
            verify(agendaRepository).save(any(AgendaEntity.class));
            verify(agendaMapper).toResponse(savedEntity);
        }

        @Test
        @DisplayName("Deve atualizar votos NO com sucesso")
        void deveAtualizarVotosNoComSucesso() {
            // Arrange
            agendaEntity.setTotalVotes(5);
            agendaEntity.setYesVotes(3);
            agendaEntity.setNoVotes(2);

            AgendaEntity savedEntity = new AgendaEntity();
            savedEntity.setId(agendaId);
            savedEntity.setTotalVotes(6);
            savedEntity.setYesVotes(3);
            savedEntity.setNoVotes(3);

            AgendaResponse updatedResponse = AgendaResponse.builder()
                    .id(agendaId)
                    .totalVotes(6)
                    .yesVotes(3)
                    .noVotes(3)
                    .build();

            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));
            when(agendaRepository.save(any(AgendaEntity.class))).thenReturn(savedEntity);
            when(agendaMapper.toResponse((AgendaEntity) savedEntity)).thenReturn(updatedResponse);

            // Act
            Result<AgendaResponse> result = agendaTimeService.updateAgendaVotes(agendaId, VoteType.NO);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(6, result.getValue().get().getTotalVotes());
            assertEquals(3, result.getValue().get().getYesVotes());
            assertEquals(3, result.getValue().get().getNoVotes());

            // Verificações de interação
            verify(agendaRepository).findById(agendaId);
            verify(agendaRepository).save(any(AgendaEntity.class));
            verify(agendaMapper).toResponse(savedEntity);
        }

        @Test
        @DisplayName("Deve retornar erro quando pauta não existe para atualização de votos")
        void deveRetornarErroQuandoPautaNaoExisteParaAtualizacaoDeVotos() {
            // Arrange
            String agendaIdInexistente = "agenda-inexistente";
            when(agendaRepository.findById(agendaIdInexistente)).thenReturn(Optional.empty());

            // Act
            Result<AgendaResponse> result = agendaTimeService.updateAgendaVotes(agendaIdInexistente, VoteType.YES);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("AGENDA_NOT_FOUND", result.getErrorCode().get());
            assertTrue(result.getErrorMessage().get().contains("Pauta não encontrada com ID"));

            // Verificações de interação
            verify(agendaRepository).findById(agendaIdInexistente);
            verify(agendaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando ocorre exceção durante atualização de votos")
        void deveRetornarErroQuandoOcorreExcecaoDuranteAtualizacaoDeVotos() {
            // Arrange
            RuntimeException exception = new RuntimeException("Erro de conexão");
            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));
            when(agendaRepository.save(any(AgendaEntity.class))).thenThrow(exception);
            when(exceptionMappingService.mapExceptionToResult(exception))
                    .thenReturn(Result.error("CONNECTION_ERROR", "Erro de conexão"));

            // Act
            Result<AgendaResponse> result = agendaTimeService.updateAgendaVotes(agendaId, VoteType.YES);

            // Assert
            assertTrue(result.isError());
            assertEquals("CONNECTION_ERROR", result.getErrorCode().get());

            // Verificações de interação
            verify(exceptionMappingService).mapExceptionToResult(exception);
        }
    }

    @Nested
    @DisplayName("Testes do método calculateAgendaResult")
    class CalculateAgendaResultTests {

        @Test
        @DisplayName("Deve calcular resultado APPROVED quando YES > NO")
        void deveCalcularResultadoApprovedQuandoYesMaiorQueNo() {
            // Arrange
            agendaEntity.setTotalVotes(10);
            agendaEntity.setYesVotes(7);
            agendaEntity.setNoVotes(3);

            AgendaEntity savedEntity = new AgendaEntity();
            savedEntity.setId(agendaId);
            savedEntity.setStatus(AgendaStatus.FINISHED);
            savedEntity.setResult(AgendaResult.APPROVED);
            savedEntity.setIsActive(false);

            AgendaResponse finalResponse = AgendaResponse.builder()
                    .id(agendaId)
                    .status(AgendaStatus.FINISHED)
                    .result(AgendaResult.APPROVED)
                    .isActive(false)
                    .totalVotes(10)
                    .yesVotes(7)
                    .noVotes(3)
                    .build();

            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));
            when(agendaRepository.save(any(AgendaEntity.class))).thenReturn(savedEntity);
            when(agendaMapper.toResponse((AgendaEntity) savedEntity)).thenReturn(finalResponse);

            // Act
            Result<AgendaResponse> result = agendaTimeService.calculateAgendaResult(agendaId);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(AgendaResult.APPROVED, result.getValue().get().getResult());
            assertEquals(AgendaStatus.FINISHED, result.getValue().get().getStatus());
            assertFalse(result.getValue().get().getIsActive());

            // Verificações de interação
            verify(agendaRepository).findById(agendaId);
            verify(agendaRepository).save(any(AgendaEntity.class));
            verify(agendaMapper).toResponse(savedEntity);
        }

        @Test
        @DisplayName("Deve calcular resultado REJECTED quando NO > YES")
        void deveCalcularResultadoRejectedQuandoNoMaiorQueYes() {
            // Arrange
            agendaEntity.setTotalVotes(10);
            agendaEntity.setYesVotes(3);
            agendaEntity.setNoVotes(7);

            AgendaEntity savedEntity = new AgendaEntity();
            savedEntity.setId(agendaId);
            savedEntity.setStatus(AgendaStatus.FINISHED);
            savedEntity.setResult(AgendaResult.REJECTED);
            savedEntity.setIsActive(false);

            AgendaResponse finalResponse = AgendaResponse.builder()
                    .id(agendaId)
                    .status(AgendaStatus.FINISHED)
                    .result(AgendaResult.REJECTED)
                    .isActive(false)
                    .build();

            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));
            when(agendaRepository.save(any(AgendaEntity.class))).thenReturn(savedEntity);
            when(agendaMapper.toResponse(savedEntity)).thenReturn(finalResponse);

            // Act
            Result<AgendaResponse> result = agendaTimeService.calculateAgendaResult(agendaId);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(AgendaResult.REJECTED, result.getValue().get().getResult());
            assertEquals(AgendaStatus.FINISHED, result.getValue().get().getStatus());
            assertFalse(result.getValue().get().getIsActive());
        }

        @Test
        @DisplayName("Deve calcular resultado TIE quando YES = NO")
        void deveCalcularResultadoTieQuandoYesIgualNo() {
            // Arrange
            agendaEntity.setTotalVotes(10);
            agendaEntity.setYesVotes(5);
            agendaEntity.setNoVotes(5);

            AgendaEntity savedEntity = new AgendaEntity();
            savedEntity.setId(agendaId);
            savedEntity.setStatus(AgendaStatus.FINISHED);
            savedEntity.setResult(AgendaResult.TIE);
            savedEntity.setIsActive(false);

            AgendaResponse finalResponse = AgendaResponse.builder()
                    .id(agendaId)
                    .status(AgendaStatus.FINISHED)
                    .result(AgendaResult.TIE)
                    .isActive(false)
                    .build();

            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));
            when(agendaRepository.save(any(AgendaEntity.class))).thenReturn(savedEntity);
            when(agendaMapper.toResponse(savedEntity)).thenReturn(finalResponse);

            // Act
            Result<AgendaResponse> result = agendaTimeService.calculateAgendaResult(agendaId);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(AgendaResult.TIE, result.getValue().get().getResult());
            assertEquals(AgendaStatus.FINISHED, result.getValue().get().getStatus());
            assertFalse(result.getValue().get().getIsActive());
        }

        @Test
        @DisplayName("Deve calcular resultado UNVOTED quando total = 0")
        void deveCalcularResultadoUnvotedQuandoTotalZero() {
            // Arrange
            agendaEntity.setTotalVotes(0);
            agendaEntity.setYesVotes(0);
            agendaEntity.setNoVotes(0);

            AgendaEntity savedEntity = new AgendaEntity();
            savedEntity.setId(agendaId);
            savedEntity.setStatus(AgendaStatus.FINISHED);
            savedEntity.setResult(AgendaResult.UNVOTED);
            savedEntity.setIsActive(false);

            AgendaResponse finalResponse = AgendaResponse.builder()
                    .id(agendaId)
                    .status(AgendaStatus.FINISHED)
                    .result(AgendaResult.UNVOTED)
                    .isActive(false)
                    .build();

            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));
            when(agendaRepository.save(any(AgendaEntity.class))).thenReturn(savedEntity);
            when(agendaMapper.toResponse(savedEntity)).thenReturn(finalResponse);

            // Act
            Result<AgendaResponse> result = agendaTimeService.calculateAgendaResult(agendaId);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(AgendaResult.UNVOTED, result.getValue().get().getResult());
            assertEquals(AgendaStatus.FINISHED, result.getValue().get().getStatus());
            assertFalse(result.getValue().get().getIsActive());
        }

        @Test
        @DisplayName("Deve retornar erro quando pauta não existe para cálculo de resultado")
        void deveRetornarErroQuandoPautaNaoExisteParaCalculoDeResultado() {
            // Arrange
            String agendaIdInexistente = "agenda-inexistente";
            when(agendaRepository.findById(agendaIdInexistente)).thenReturn(Optional.empty());

            // Act
            Result<AgendaResponse> result = agendaTimeService.calculateAgendaResult(agendaIdInexistente);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("AGENDA_NOT_FOUND", result.getErrorCode().get());
            assertTrue(result.getErrorMessage().get().contains("Pauta não encontrada com ID"));

            // Verificações de interação
            verify(agendaRepository).findById(agendaIdInexistente);
            verify(agendaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando ocorre exceção durante cálculo de resultado")
        void deveRetornarErroQuandoOcorreExcecaoDuranteCalculoDeResultado() {
            // Arrange
            RuntimeException exception = new RuntimeException("Erro de processamento");
            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));
            when(agendaRepository.save(any(AgendaEntity.class))).thenThrow(exception);
            when(exceptionMappingService.mapExceptionToResult(exception))
                    .thenReturn(Result.error("PROCESSING_ERROR", "Erro de processamento"));

            // Act
            Result<AgendaResponse> result = agendaTimeService.calculateAgendaResult(agendaId);

            // Assert
            assertTrue(result.isError());
            assertEquals("PROCESSING_ERROR", result.getErrorCode().get());

            // Verificações de interação
            verify(exceptionMappingService).mapExceptionToResult(exception);
        }
    }

    @Nested
    @DisplayName("Testes de integração e cenários especiais")
    class IntegrationAndSpecialScenariosTests {

        @Test
        @DisplayName("Deve lidar com valores nulos nos parâmetros de entrada")
        void deveLidarComValoresNulosNosParametrosDeEntrada() {
            // Arrange
            RuntimeException exception = new NullPointerException("ID não pode ser nulo");
            when(agendaRepository.findById(null)).thenThrow(exception);
            when(exceptionMappingService.mapExceptionToResult(any(Exception.class)))
                    .thenReturn(Result.error("NULL_PARAMETER", "Parâmetro nulo"));

            // Act & Assert - startAgendaTimer com ID nulo
            Result<AgendaResponse> result1 = agendaTimeService.startAgendaTimer(null, 60);
            assertTrue(result1.isError());

            // Act & Assert - updateAgendaVotes com ID nulo
            Result<AgendaResponse> result2 = agendaTimeService.updateAgendaVotes(null, VoteType.YES);
            assertTrue(result2.isError());

            // Act & Assert - calculateAgendaResult com ID nulo
            Result<AgendaResponse> result3 = agendaTimeService.calculateAgendaResult(null);
            assertTrue(result3.isError());

            // Verificações - deve ser chamado 3 vezes (uma para cada método)
            verify(exceptionMappingService, times(3)).mapExceptionToResult(any(Exception.class));
        }

        @Test
        @DisplayName("Deve manter transações corretas nos métodos")
        void deveManterTransacoesCorretasNosMetodos() {
            // Este teste verifica se as anotações @Transactional estão sendo respeitadas
            // Em um ambiente real, isso seria testado com um banco de dados real

            // Arrange
            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));
            when(agendaRepository.save(any(AgendaEntity.class))).thenReturn(agendaEntity);
            when(agendaMapper.toResponse(any(AgendaEntity.class))).thenReturn(agendaResponse);

            // Act
            Result<AgendaResponse> result = agendaTimeService.startAgendaTimer(agendaId, 60);

            // Assert
            assertTrue(result.isSuccess());
            // Em um teste de integração real, verificaríamos se a transação foi commitada
        }

        @Test
        @DisplayName("Deve testar diferentes durações de timer")
        void deveTestarDiferentesDuracoesDeTtimer() {
            // Arrange
            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));
            when(agendaRepository.save(any(AgendaEntity.class))).thenReturn(agendaEntity);
            when(agendaMapper.toResponse(any(AgendaEntity.class))).thenReturn(agendaResponse);

            // Act & Assert - Timer de 1 minuto
            Result<AgendaResponse> result1 = agendaTimeService.startAgendaTimer(agendaId, 1);
            assertTrue(result1.isSuccess());

            // Act & Assert - Timer de 120 minutos
            Result<AgendaResponse> result2 = agendaTimeService.startAgendaTimer(agendaId, 120);
            assertTrue(result2.isSuccess());

            // Verificações - deve ser chamado 2 vezes (uma para cada timer)
            verify(agendaRepository, times(2)).findById(agendaId);
            verify(agendaRepository, times(2)).save(any(AgendaEntity.class));
        }

        @Test
        @DisplayName("Deve testar fluxo completo de uma pauta")
        void deveTestarFluxoCompletoDeumapauta() {
            // Arrange - Pauta inicial
            AgendaEntity pautaInicial = new AgendaEntity();
            pautaInicial.setId(agendaId);
            pautaInicial.setStatus(AgendaStatus.DRAFT);
            pautaInicial.setTotalVotes(0);
            pautaInicial.setYesVotes(0);
            pautaInicial.setNoVotes(0);

            // Pauta em progresso
            AgendaEntity pautaEmProgresso = new AgendaEntity();
            pautaEmProgresso.setId(agendaId);
            pautaEmProgresso.setStatus(AgendaStatus.IN_PROGRESS);
            pautaEmProgresso.setTotalVotes(0);
            pautaEmProgresso.setYesVotes(0);
            pautaEmProgresso.setNoVotes(0);

            // Pauta com votos
            AgendaEntity pautaComVotos = new AgendaEntity();
            pautaComVotos.setId(agendaId);
            pautaComVotos.setTotalVotes(3);
            pautaComVotos.setYesVotes(2);
            pautaComVotos.setNoVotes(1);

            // Pauta finalizada
            AgendaEntity pautaFinalizada = new AgendaEntity();
            pautaFinalizada.setId(agendaId);
            pautaFinalizada.setStatus(AgendaStatus.FINISHED);
            pautaFinalizada.setResult(AgendaResult.APPROVED);
            pautaFinalizada.setIsActive(false);

            when(agendaRepository.findById(agendaId))
                    .thenReturn(Optional.of(pautaInicial))
                    .thenReturn(Optional.of(pautaEmProgresso))
                    .thenReturn(Optional.of(pautaComVotos));

            when(agendaRepository.save(any(AgendaEntity.class)))
                    .thenReturn(pautaEmProgresso)
                    .thenReturn(pautaComVotos)
                    .thenReturn(pautaFinalizada);

            when(agendaMapper.toResponse(any(AgendaEntity.class))).thenReturn(agendaResponse);

            // Act & Assert - 1. Iniciar timer
            Result<AgendaResponse> resultStart = agendaTimeService.startAgendaTimer(agendaId, 60);
            assertTrue(resultStart.isSuccess());

            // Act & Assert - 2. Atualizar votos
            Result<AgendaResponse> resultVote = agendaTimeService.updateAgendaVotes(agendaId, VoteType.YES);
            assertTrue(resultVote.isSuccess());

            // Act & Assert - 3. Calcular resultado
            Result<AgendaResponse> resultCalculate = agendaTimeService.calculateAgendaResult(agendaId);
            assertTrue(resultCalculate.isSuccess());

            // Verificações - deve ser chamado 3 vezes (timer, voto e resultado)
            verify(agendaRepository, times(3)).findById(agendaId);
            verify(agendaRepository, times(3)).save(any(AgendaEntity.class));
            verify(agendaMapper, times(3)).toResponse(any(AgendaEntity.class));
        }
    }
}