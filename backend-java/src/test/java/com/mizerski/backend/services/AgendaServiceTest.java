package com.mizerski.backend.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.mizerski.backend.dtos.request.CreateAgendaRequest;
import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.dtos.response.PagedResponse;
import com.mizerski.backend.models.domains.Agendas;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.entities.AgendaEntity;
import com.mizerski.backend.models.enums.AgendaCategory;
import com.mizerski.backend.models.enums.AgendaResult;
import com.mizerski.backend.models.enums.AgendaStatus;
import com.mizerski.backend.models.mappers.AgendaMapper;
import com.mizerski.backend.repositories.AgendaRepository;

/**
 * Testes unitários para o serviço de pautas
 * 
 * Testa todos os métodos do AgendaService incluindo cenários de sucesso e erro
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AgendaService - Testes Unitários")
class AgendaServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private AgendaMapper agendaMapper;

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private ExceptionMappingService exceptionMappingService;

    @InjectMocks
    private AgendaServiceImpl agendaService;

    private CreateAgendaRequest createAgendaRequest;
    private AgendaEntity agendaEntity;
    private AgendaResponse agendaResponse;
    private Agendas agendaDomain;
    private String idempotencyKey;

    /**
     * Configuração inicial dos dados de teste
     */
    @BeforeEach
    void setUp() {
        // Dados de request
        createAgendaRequest = new CreateAgendaRequest();
        createAgendaRequest.setTitle("Pauta de Teste");
        createAgendaRequest.setDescription("Descrição da pauta de teste para votação");
        createAgendaRequest.setCategory(AgendaCategory.PROJETOS);

        // Entidade de agenda
        agendaEntity = new AgendaEntity();
        agendaEntity.setId("agenda-123");
        agendaEntity.setTitle("Pauta de Teste");
        agendaEntity.setDescription("Descrição da pauta de teste para votação");
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
                .id("agenda-123")
                .title("Pauta de Teste")
                .description("Descrição da pauta de teste para votação")
                .category(AgendaCategory.PROJETOS)
                .status(AgendaStatus.DRAFT)
                .result(AgendaResult.UNVOTED)
                .totalVotes(0)
                .yesVotes(0)
                .noVotes(0)
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Domínio de agenda
        agendaDomain = Agendas.builder()
                .id("agenda-123")
                .title("Pauta de Teste")
                .description("Descrição da pauta de teste para votação")
                .category(AgendaCategory.PROJETOS)
                .status(AgendaStatus.DRAFT)
                .result(AgendaResult.UNVOTED)
                .totalVotes(0)
                .yesVotes(0)
                .noVotes(0)
                .isActive(false)
                .build();

        // Chave de idempotência
        idempotencyKey = "createAgenda:Pauta de Teste:Descrição da pauta de teste para votação";
    }

    @Nested
    @DisplayName("Testes do método createAgenda")
    class CreateAgendaTests {

        @Test
        @DisplayName("Deve criar pauta com sucesso quando dados válidos")
        void deveCriarPautaComSucessoQuandoDadosValidos() {
            // Arrange
            when(idempotencyService.generateKey("createAgenda",
                    createAgendaRequest.getTitle(), createAgendaRequest.getDescription()))
                    .thenReturn(idempotencyKey);
            when(idempotencyService.checkIdempotency(idempotencyKey))
                    .thenReturn(Result.error("NOT_FOUND", "Operação não encontrada no cache"));
            when(agendaRepository.existsByTitle(createAgendaRequest.getTitle())).thenReturn(false);
            when(agendaMapper.fromCreateRequest(createAgendaRequest)).thenReturn(agendaDomain);
            when(agendaMapper.toEntity(agendaDomain)).thenReturn(agendaEntity);
            when(agendaRepository.save(any(AgendaEntity.class))).thenReturn(agendaEntity);
            when(agendaMapper.toResponse(agendaEntity)).thenReturn(agendaResponse);

            // Act
            Result<AgendaResponse> result = agendaService.createAgenda(createAgendaRequest);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(agendaResponse, result.getValue().get());
            assertEquals("Pauta de Teste", result.getValue().get().getTitle());
            assertEquals(AgendaStatus.DRAFT, result.getValue().get().getStatus());
            assertFalse(result.getValue().get().getIsActive());

            // Verificações de interação
            verify(idempotencyService).generateKey("createAgenda",
                    createAgendaRequest.getTitle(), createAgendaRequest.getDescription());
            verify(idempotencyService).checkIdempotency(idempotencyKey);
            verify(agendaRepository).existsByTitle(createAgendaRequest.getTitle());
            verify(agendaMapper).fromCreateRequest(createAgendaRequest);
            verify(agendaMapper).toEntity(agendaDomain);
            verify(agendaRepository).save(any(AgendaEntity.class));
            verify(agendaMapper).toResponse(agendaEntity);
            verify(idempotencyService).storeResult(eq(idempotencyKey), eq(agendaResponse), eq(600));
        }

        @Test
        @DisplayName("Deve retornar resultado do cache quando operação já foi executada")
        void deveRetornarResultadoDoCacheQuandoOperacaoJaFoiExecutada() {
            // Arrange
            when(idempotencyService.generateKey("createAgenda",
                    createAgendaRequest.getTitle(), createAgendaRequest.getDescription()))
                    .thenReturn(idempotencyKey);
            when(idempotencyService.checkIdempotency(idempotencyKey))
                    .thenReturn(Result.success(agendaResponse));

            // Act
            Result<AgendaResponse> result = agendaService.createAgenda(createAgendaRequest);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(agendaResponse, result.getValue().get());

            // Verificações de interação
            verify(idempotencyService).generateKey("createAgenda",
                    createAgendaRequest.getTitle(), createAgendaRequest.getDescription());
            verify(idempotencyService).checkIdempotency(idempotencyKey);
            verify(agendaRepository, never()).existsByTitle(any());
            verify(agendaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando título já existe")
        void deveRetornarErroQuandoTituloJaExiste() {
            // Arrange
            when(idempotencyService.generateKey("createAgenda",
                    createAgendaRequest.getTitle(), createAgendaRequest.getDescription()))
                    .thenReturn(idempotencyKey);
            when(idempotencyService.checkIdempotency(idempotencyKey))
                    .thenReturn(Result.error("NOT_FOUND", "Operação não encontrada no cache"));
            when(agendaRepository.existsByTitle(createAgendaRequest.getTitle())).thenReturn(true);

            // Act
            Result<AgendaResponse> result = agendaService.createAgenda(createAgendaRequest);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("DUPLICATE_TITLE", result.getErrorCode().get());
            assertTrue(result.getErrorMessage().get().contains("Já existe uma pauta com este título"));

            // Verificações de interação
            verify(agendaRepository).existsByTitle(createAgendaRequest.getTitle());
            verify(agendaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando ocorre exceção durante criação")
        void deveRetornarErroQuandoOcorreExcecaoDuranteCriacao() {
            // Arrange
            RuntimeException exception = new RuntimeException("Erro de banco de dados");
            when(idempotencyService.generateKey("createAgenda",
                    createAgendaRequest.getTitle(), createAgendaRequest.getDescription()))
                    .thenReturn(idempotencyKey);
            when(idempotencyService.checkIdempotency(idempotencyKey))
                    .thenReturn(Result.error("NOT_FOUND", "Operação não encontrada no cache"));
            when(agendaRepository.existsByTitle(createAgendaRequest.getTitle())).thenReturn(false);
            when(agendaMapper.fromCreateRequest(createAgendaRequest)).thenThrow(exception);
            when(exceptionMappingService.mapExceptionToResult(exception))
                    .thenReturn(Result.error("INTERNAL_ERROR", "Erro interno do servidor"));

            // Act
            Result<AgendaResponse> result = agendaService.createAgenda(createAgendaRequest);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("INTERNAL_ERROR", result.getErrorCode().get());

            // Verificações de interação
            verify(exceptionMappingService).mapExceptionToResult(exception);
        }
    }

    @Nested
    @DisplayName("Testes do método getAgendaById")
    class GetAgendaByIdTests {

        @Test
        @DisplayName("Deve buscar pauta por ID com sucesso quando pauta existe")
        void deveBuscarPautaPorIdComSucessoQuandoPautaExiste() {
            // Arrange
            String agendaId = "agenda-123";
            when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaEntity));
            when(agendaMapper.toResponse(agendaEntity)).thenReturn(agendaResponse);

            // Act
            Result<AgendaResponse> result = agendaService.getAgendaById(agendaId);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isPresent());
            assertEquals(agendaResponse, result.getValue().get());
            assertEquals(agendaId, result.getValue().get().getId());

            // Verificações de interação
            verify(agendaRepository).findById(agendaId);
            verify(agendaMapper).toResponse(agendaEntity);
        }

        @Test
        @DisplayName("Deve retornar erro quando pauta não existe")
        void deveRetornarErroQuandoPautaNaoExiste() {
            // Arrange
            String agendaId = "agenda-inexistente";
            when(agendaRepository.findById(agendaId)).thenReturn(Optional.empty());

            // Act
            Result<AgendaResponse> result = agendaService.getAgendaById(agendaId);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("AGENDA_NOT_FOUND", result.getErrorCode().get());
            assertTrue(result.getErrorMessage().get().contains("Pauta não encontrada com ID"));

            // Verificações de interação
            verify(agendaRepository).findById(agendaId);
            verify(agendaMapper, never()).toResponse(any(AgendaEntity.class));
        }

        @Test
        @DisplayName("Deve retornar erro quando ocorre exceção durante busca")
        void deveRetornarErroQuandoOcorreExcecaoDuranteBusca() {
            // Arrange
            String agendaId = "agenda-123";
            RuntimeException exception = new RuntimeException("Erro de banco de dados");
            when(agendaRepository.findById(agendaId)).thenThrow(exception);
            when(exceptionMappingService.mapExceptionToResult(exception))
                    .thenReturn(Result.error("INTERNAL_ERROR", "Erro interno do servidor"));

            // Act
            Result<AgendaResponse> result = agendaService.getAgendaById(agendaId);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("INTERNAL_ERROR", result.getErrorCode().get());

            // Verificações de interação
            verify(agendaRepository).findById(agendaId);
            verify(exceptionMappingService).mapExceptionToResult(exception);
        }
    }

    @Nested
    @DisplayName("Testes do método getAllAgendas (sem paginação)")
    class GetAllAgendasTests {

        @Test
        @DisplayName("Deve buscar todas as pautas com sucesso")
        void deveBuscarTodasPautasComSucesso() {
            // Arrange
            AgendaEntity agenda2 = new AgendaEntity();
            agenda2.setId("agenda-456");
            agenda2.setTitle("Segunda Pauta");
            agenda2.setStatus(AgendaStatus.OPEN);

            AgendaResponse response2 = AgendaResponse.builder()
                    .id("agenda-456")
                    .title("Segunda Pauta")
                    .status(AgendaStatus.OPEN)
                    .build();

            List<AgendaEntity> agendaEntities = Arrays.asList(agendaEntity, agenda2);
            when(agendaRepository.findAll()).thenReturn(agendaEntities);
            when(agendaMapper.toResponse(agendaEntity)).thenReturn(agendaResponse);
            when(agendaMapper.toResponse(agenda2)).thenReturn(response2);

            // Act
            List<AgendaResponse> result = agendaService.getAllAgendas();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(agendaResponse, result.get(0));
            assertEquals(response2, result.get(1));

            // Verificações de interação
            verify(agendaRepository).findAll();
            verify(agendaMapper).toResponse(agendaEntity);
            verify(agendaMapper).toResponse(agenda2);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há pautas")
        void deveRetornarListaVaziaQuandoNaoHaPautas() {
            // Arrange
            when(agendaRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<AgendaResponse> result = agendaService.getAllAgendas();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            // Verificações de interação
            verify(agendaRepository).findAll();
        }
    }

    @Nested
    @DisplayName("Testes do método getAllAgendas (com paginação)")
    class GetAllAgendasPagedTests {

        @Test
        @DisplayName("Deve buscar pautas com paginação com sucesso")
        void deveBuscarPautasComPaginacaoComSucesso() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<AgendaEntity> agendaEntities = Arrays.asList(agendaEntity);
            Page<AgendaEntity> page = new PageImpl<>(agendaEntities, pageable, 1);

            when(agendaRepository.findAll(pageable)).thenReturn(page);
            when(agendaMapper.toResponse(agendaEntity)).thenReturn(agendaResponse);

            // Act
            PagedResponse<AgendaResponse> result = agendaService.getAllAgendas(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(0, result.getPage());
            assertEquals(10, result.getSize());
            assertEquals(1, result.getTotalElements());
            assertEquals(agendaResponse, result.getContent().get(0));

            // Verificações de interação
            verify(agendaRepository).findAll(pageable);
            verify(agendaMapper).toResponse(agendaEntity);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não há pautas")
        void deveRetornarPaginaVaziaQuandoNaoHaPautas() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<AgendaEntity> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(agendaRepository.findAll(pageable)).thenReturn(page);

            // Act
            PagedResponse<AgendaResponse> result = agendaService.getAllAgendas(pageable);

            // Assert
            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getPage());
            assertEquals(10, result.getSize());
            assertEquals(0, result.getTotalElements());

            // Verificações de interação
            verify(agendaRepository).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Testes do método getAllAgendasWithOpenSessions (sem paginação)")
    class GetAllAgendasWithOpenSessionsTests {

        @Test
        @DisplayName("Deve buscar pautas com sessões abertas com sucesso")
        void deveBuscarPautasComSessoesAbertasComSucesso() {
            // Arrange
            AgendaEntity agendaAberta = new AgendaEntity();
            agendaAberta.setId("agenda-aberta");
            agendaAberta.setTitle("Pauta Aberta");
            agendaAberta.setStatus(AgendaStatus.OPEN);

            AgendaEntity agendaEmAndamento = new AgendaEntity();
            agendaEmAndamento.setId("agenda-andamento");
            agendaEmAndamento.setTitle("Pauta em Andamento");
            agendaEmAndamento.setStatus(AgendaStatus.IN_PROGRESS);

            AgendaResponse responseAberta = AgendaResponse.builder()
                    .id("agenda-aberta")
                    .title("Pauta Aberta")
                    .status(AgendaStatus.OPEN)
                    .build();

            AgendaResponse responseAndamento = AgendaResponse.builder()
                    .id("agenda-andamento")
                    .title("Pauta em Andamento")
                    .status(AgendaStatus.IN_PROGRESS)
                    .build();

            List<AgendaEntity> agendaEntities = Arrays.asList(agendaAberta, agendaEmAndamento);
            List<AgendaStatus> statusList = Arrays.asList(AgendaStatus.OPEN, AgendaStatus.IN_PROGRESS);

            when(agendaRepository.findByStatusIn(statusList)).thenReturn(agendaEntities);
            when(agendaMapper.toResponse(agendaAberta)).thenReturn(responseAberta);
            when(agendaMapper.toResponse(agendaEmAndamento)).thenReturn(responseAndamento);

            // Act
            List<AgendaResponse> result = agendaService.getAllAgendasWithOpenSessions();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(responseAberta, result.get(0));
            assertEquals(responseAndamento, result.get(1));

            // Verificações de interação
            verify(agendaRepository).findByStatusIn(statusList);
            verify(agendaMapper).toResponse(agendaAberta);
            verify(agendaMapper).toResponse(agendaEmAndamento);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há pautas abertas")
        void deveRetornarListaVaziaQuandoNaoHaPautasAbertas() {
            // Arrange
            List<AgendaStatus> statusList = Arrays.asList(AgendaStatus.OPEN, AgendaStatus.IN_PROGRESS);
            when(agendaRepository.findByStatusIn(statusList)).thenReturn(Collections.emptyList());

            // Act
            List<AgendaResponse> result = agendaService.getAllAgendasWithOpenSessions();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            // Verificações de interação
            verify(agendaRepository).findByStatusIn(statusList);
        }
    }

    @Nested
    @DisplayName("Testes do método getAllAgendasWithOpenSessions (com paginação)")
    class GetAllAgendasWithOpenSessionsPagedTests {

        @Test
        @DisplayName("Deve buscar pautas abertas com paginação com sucesso")
        void deveBuscarPautasAbertasComPaginacaoComSucesso() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            AgendaEntity agendaAberta = new AgendaEntity();
            agendaAberta.setId("agenda-aberta");
            agendaAberta.setStatus(AgendaStatus.OPEN);

            AgendaResponse responseAberta = AgendaResponse.builder()
                    .id("agenda-aberta")
                    .status(AgendaStatus.OPEN)
                    .build();

            List<AgendaEntity> agendaEntities = Arrays.asList(agendaAberta);
            List<AgendaStatus> statusList = Arrays.asList(AgendaStatus.OPEN, AgendaStatus.IN_PROGRESS);
            Page<AgendaEntity> page = new PageImpl<>(agendaEntities, pageable, 1);

            when(agendaRepository.findByStatusIn(statusList, pageable)).thenReturn(page);
            when(agendaMapper.toResponse(agendaAberta)).thenReturn(responseAberta);

            // Act
            PagedResponse<AgendaResponse> result = agendaService.getAllAgendasWithOpenSessions(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(0, result.getPage());
            assertEquals(10, result.getSize());
            assertEquals(1, result.getTotalElements());
            assertEquals(responseAberta, result.getContent().get(0));

            // Verificações de interação
            verify(agendaRepository).findByStatusIn(statusList, pageable);
            verify(agendaMapper).toResponse(agendaAberta);
        }
    }

    @Nested
    @DisplayName("Testes do método getAllAgendasFinished (sem paginação)")
    class GetAllAgendasFinishedTests {

        @Test
        @DisplayName("Deve buscar pautas encerradas com sucesso")
        void deveBuscarPautasEncerradasComSucesso() {
            // Arrange
            AgendaEntity agendaFinalizada = new AgendaEntity();
            agendaFinalizada.setId("agenda-finalizada");
            agendaFinalizada.setTitle("Pauta Finalizada");
            agendaFinalizada.setStatus(AgendaStatus.FINISHED);

            AgendaEntity agendaCancelada = new AgendaEntity();
            agendaCancelada.setId("agenda-cancelada");
            agendaCancelada.setTitle("Pauta Cancelada");
            agendaCancelada.setStatus(AgendaStatus.CANCELLED);

            AgendaResponse responseFinalizada = AgendaResponse.builder()
                    .id("agenda-finalizada")
                    .title("Pauta Finalizada")
                    .status(AgendaStatus.FINISHED)
                    .build();

            AgendaResponse responseCancelada = AgendaResponse.builder()
                    .id("agenda-cancelada")
                    .title("Pauta Cancelada")
                    .status(AgendaStatus.CANCELLED)
                    .build();

            List<AgendaEntity> agendaEntities = Arrays.asList(agendaFinalizada, agendaCancelada);
            List<AgendaStatus> statusList = Arrays.asList(AgendaStatus.FINISHED, AgendaStatus.CANCELLED);

            when(agendaRepository.findByStatusIn(statusList)).thenReturn(agendaEntities);
            when(agendaMapper.toResponse(agendaFinalizada)).thenReturn(responseFinalizada);
            when(agendaMapper.toResponse(agendaCancelada)).thenReturn(responseCancelada);

            // Act
            List<AgendaResponse> result = agendaService.getAllAgendasFinished();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(responseFinalizada, result.get(0));
            assertEquals(responseCancelada, result.get(1));

            // Verificações de interação
            verify(agendaRepository).findByStatusIn(statusList);
            verify(agendaMapper).toResponse(agendaFinalizada);
            verify(agendaMapper).toResponse(agendaCancelada);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há pautas encerradas")
        void deveRetornarListaVaziaQuandoNaoHaPautasEncerradas() {
            // Arrange
            List<AgendaStatus> statusList = Arrays.asList(AgendaStatus.FINISHED, AgendaStatus.CANCELLED);
            when(agendaRepository.findByStatusIn(statusList)).thenReturn(Collections.emptyList());

            // Act
            List<AgendaResponse> result = agendaService.getAllAgendasFinished();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());

            // Verificações de interação
            verify(agendaRepository).findByStatusIn(statusList);
        }
    }

    @Nested
    @DisplayName("Testes do método getAllAgendasFinished (com paginação)")
    class GetAllAgendasFinishedPagedTests {

        @Test
        @DisplayName("Deve buscar pautas encerradas com paginação com sucesso")
        void deveBuscarPautasEncerradasComPaginacaoComSucesso() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            AgendaEntity agendaFinalizada = new AgendaEntity();
            agendaFinalizada.setId("agenda-finalizada");
            agendaFinalizada.setStatus(AgendaStatus.FINISHED);

            AgendaResponse responseFinalizada = AgendaResponse.builder()
                    .id("agenda-finalizada")
                    .status(AgendaStatus.FINISHED)
                    .build();

            List<AgendaEntity> agendaEntities = Arrays.asList(agendaFinalizada);
            List<AgendaStatus> statusList = Arrays.asList(AgendaStatus.FINISHED, AgendaStatus.CANCELLED);
            Page<AgendaEntity> page = new PageImpl<>(agendaEntities, pageable, 1);

            when(agendaRepository.findByStatusIn(statusList, pageable)).thenReturn(page);
            when(agendaMapper.toResponse(agendaFinalizada)).thenReturn(responseFinalizada);

            // Act
            PagedResponse<AgendaResponse> result = agendaService.getAllAgendasFinished(pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(0, result.getPage());
            assertEquals(10, result.getSize());
            assertEquals(1, result.getTotalElements());
            assertEquals(responseFinalizada, result.getContent().get(0));

            // Verificações de interação
            verify(agendaRepository).findByStatusIn(statusList, pageable);
            verify(agendaMapper).toResponse(agendaFinalizada);
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não há pautas encerradas")
        void deveRetornarPaginaVaziaQuandoNaoHaPautasEncerradas() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<AgendaStatus> statusList = Arrays.asList(AgendaStatus.FINISHED, AgendaStatus.CANCELLED);
            Page<AgendaEntity> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(agendaRepository.findByStatusIn(statusList, pageable)).thenReturn(page);

            // Act
            PagedResponse<AgendaResponse> result = agendaService.getAllAgendasFinished(pageable);

            // Assert
            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getPage());
            assertEquals(10, result.getSize());
            assertEquals(0, result.getTotalElements());

            // Verificações de interação
            verify(agendaRepository).findByStatusIn(statusList, pageable);
        }
    }

    @Nested
    @DisplayName("Testes de integração e cenários especiais")
    class IntegrationAndSpecialScenariosTests {

        @Test
        @DisplayName("Deve testar diferentes categorias de agenda")
        void deveTestarDiferentesCategoriasDePauta() {
            // Arrange
            CreateAgendaRequest requestAdministrativo = new CreateAgendaRequest();
            requestAdministrativo.setTitle("Pauta Administrativa");
            requestAdministrativo.setDescription("Descrição administrativa");
            requestAdministrativo.setCategory(AgendaCategory.ADMINISTRATIVO);

            CreateAgendaRequest requestFinanceiro = new CreateAgendaRequest();
            requestFinanceiro.setTitle("Pauta Financeira");
            requestFinanceiro.setDescription("Descrição financeira");
            requestFinanceiro.setCategory(AgendaCategory.FINANCEIRO);

            // Configurar mocks para ambas as categorias
            when(idempotencyService.generateKey(anyString(), anyString(), anyString()))
                    .thenReturn("test-key");
            when(idempotencyService.checkIdempotency(anyString()))
                    .thenReturn(Result.error("NOT_FOUND", "Operação não encontrada no cache"));
            when(agendaRepository.existsByTitle(anyString())).thenReturn(false);
            when(agendaMapper.fromCreateRequest(any())).thenReturn(agendaDomain);
            when(agendaMapper.toEntity(any())).thenReturn(agendaEntity);
            when(agendaRepository.save(any())).thenReturn(agendaEntity);
            when(agendaMapper.toResponse(any(AgendaEntity.class))).thenReturn(agendaResponse);

            // Act & Assert - Categoria Administrativa
            Result<AgendaResponse> resultAdmin = agendaService.createAgenda(requestAdministrativo);
            assertTrue(resultAdmin.isSuccess());

            // Act & Assert - Categoria Financeira
            Result<AgendaResponse> resultFinanceiro = agendaService.createAgenda(requestFinanceiro);
            assertTrue(resultFinanceiro.isSuccess());

            // Verificações - deve ser chamado 2 vezes (uma para cada categoria)
            verify(agendaRepository, times(2)).save(any(AgendaEntity.class));
        }

        @Test
        @DisplayName("Deve lidar com valores nulos nos parâmetros de entrada")
        void deveLidarComValoresNulosNosParametrosDeEntrada() {
            // Arrange
            RuntimeException exception = new NullPointerException("ID não pode ser nulo");
            when(agendaRepository.findById(null)).thenThrow(exception);
            when(exceptionMappingService.mapExceptionToResult(exception))
                    .thenReturn(Result.error("INVALID_PARAMETER", "Parâmetro inválido"));

            // Act
            Result<AgendaResponse> resultIdNulo = agendaService.getAgendaById(null);

            // Assert
            assertTrue(resultIdNulo.isError());

            // Verificações
            verify(exceptionMappingService).mapExceptionToResult(any(Exception.class));
        }

        @Test
        @DisplayName("Deve manter transações corretas nos métodos")
        void deveManterTransacoesCorretasNosMetodos() {
            // Arrange
            when(agendaRepository.findAll()).thenReturn(Arrays.asList(agendaEntity));
            when(agendaMapper.toResponse(agendaEntity)).thenReturn(agendaResponse);

            // Act
            List<AgendaResponse> result = agendaService.getAllAgendas();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());

            // Verificações de que métodos de leitura não fazem alterações
            verify(agendaRepository, never()).save(any());
            verify(agendaRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Deve validar comportamento com diferentes status de agenda")
        void deveValidarComportamentoComDiferentesStatusDePauta() {
            // Arrange
            AgendaEntity agendaDraft = new AgendaEntity();
            agendaDraft.setStatus(AgendaStatus.DRAFT);

            AgendaEntity agendaOpen = new AgendaEntity();
            agendaOpen.setStatus(AgendaStatus.OPEN);

            AgendaEntity agendaFinished = new AgendaEntity();
            agendaFinished.setStatus(AgendaStatus.FINISHED);

            List<AgendaEntity> todasAgendas = Arrays.asList(agendaDraft, agendaOpen, agendaFinished);
            List<AgendaEntity> agendasAbertas = Arrays.asList(agendaOpen);
            List<AgendaEntity> agendasEncerradas = Arrays.asList(agendaFinished);

            when(agendaRepository.findAll()).thenReturn(todasAgendas);
            when(agendaRepository.findByStatusIn(Arrays.asList(AgendaStatus.OPEN, AgendaStatus.IN_PROGRESS)))
                    .thenReturn(agendasAbertas);
            when(agendaRepository.findByStatusIn(Arrays.asList(AgendaStatus.FINISHED, AgendaStatus.CANCELLED)))
                    .thenReturn(agendasEncerradas);

            when(agendaMapper.toResponse(any(AgendaEntity.class))).thenReturn(agendaResponse);

            // Act
            List<AgendaResponse> todasPautas = agendaService.getAllAgendas();
            List<AgendaResponse> pautasAbertas = agendaService.getAllAgendasWithOpenSessions();
            List<AgendaResponse> pautasEncerradas = agendaService.getAllAgendasFinished();

            // Assert
            assertEquals(3, todasPautas.size());
            assertEquals(1, pautasAbertas.size());
            assertEquals(1, pautasEncerradas.size());

            // Verificações
            verify(agendaRepository).findAll();
            verify(agendaRepository).findByStatusIn(Arrays.asList(AgendaStatus.OPEN, AgendaStatus.IN_PROGRESS));
            verify(agendaRepository).findByStatusIn(Arrays.asList(AgendaStatus.FINISHED, AgendaStatus.CANCELLED));
        }

        @Test
        @DisplayName("Deve testar comportamento de idempotência com diferentes chaves")
        void deveTestarComportamentoDeIdempotenciaComDiferentesChaves() {
            // Arrange
            CreateAgendaRequest request1 = new CreateAgendaRequest();
            request1.setTitle("Pauta 1");
            request1.setDescription("Descrição 1");
            request1.setCategory(AgendaCategory.PROJETOS);

            CreateAgendaRequest request2 = new CreateAgendaRequest();
            request2.setTitle("Pauta 2");
            request2.setDescription("Descrição 2");
            request2.setCategory(AgendaCategory.ADMINISTRATIVO);

            String key1 = "createAgenda:Pauta 1:Descrição 1";
            String key2 = "createAgenda:Pauta 2:Descrição 2";

            when(idempotencyService.generateKey("createAgenda", "Pauta 1", "Descrição 1"))
                    .thenReturn(key1);
            when(idempotencyService.generateKey("createAgenda", "Pauta 2", "Descrição 2"))
                    .thenReturn(key2);
            when(idempotencyService.checkIdempotency(key1))
                    .thenReturn(Result.error("NOT_FOUND", "Operação não encontrada no cache"));
            when(idempotencyService.checkIdempotency(key2))
                    .thenReturn(Result.success(agendaResponse));

            when(agendaRepository.existsByTitle("Pauta 1")).thenReturn(false);
            when(agendaMapper.fromCreateRequest(request1)).thenReturn(agendaDomain);
            when(agendaMapper.toEntity(agendaDomain)).thenReturn(agendaEntity);
            when(agendaRepository.save(agendaEntity)).thenReturn(agendaEntity);
            when(agendaMapper.toResponse(agendaEntity)).thenReturn(agendaResponse);

            // Act
            Result<AgendaResponse> result1 = agendaService.createAgenda(request1);
            Result<AgendaResponse> result2 = agendaService.createAgenda(request2);

            // Assert
            assertTrue(result1.isSuccess());
            assertTrue(result2.isSuccess());

            // Verificações
            verify(idempotencyService).generateKey("createAgenda", "Pauta 1", "Descrição 1");
            verify(idempotencyService).generateKey("createAgenda", "Pauta 2", "Descrição 2");
            verify(idempotencyService).checkIdempotency(key1);
            verify(idempotencyService).checkIdempotency(key2);
            verify(agendaRepository).save(agendaEntity); // Apenas para request1
            verify(idempotencyService).storeResult(eq(key1), eq(agendaResponse), eq(600));
        }
    }
}