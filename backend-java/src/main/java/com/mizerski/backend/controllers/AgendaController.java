package com.mizerski.backend.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mizerski.backend.annotations.ValidUUID;
import com.mizerski.backend.dtos.request.CreateAgendaRequest;
import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.dtos.response.PagedResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.services.AgendaService;
import com.mizerski.backend.services.ErrorMappingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Controller para gerenciar operações relacionadas a pautas.
 */
@RestController
@RequestMapping("/api/v1/agendas")
@Validated
@Tag(name = "Agendas", description = "Operações relacionadas a pautas de votação")
public class AgendaController extends BaseController {

    private final AgendaService agendaService;

    /**
     * Construtor para injeção de dependência via construtor
     * 
     * @param errorMappingService Serviço de mapeamento de erros
     * @param agendaService       Serviço de pautas
     */
    public AgendaController(ErrorMappingService errorMappingService, AgendaService agendaService) {
        super(errorMappingService);
        this.agendaService = agendaService;
    }

    /**
     * Cria uma nova pauta com tratamento de idempotência
     */
    @PostMapping
    @Operation(summary = "Criar nova pauta", description = "Cria uma nova pauta com tratamento de idempotência")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Pauta já existe")
    })
    public ResponseEntity<?> createAgenda(@Valid @RequestBody CreateAgendaRequest request) {
        logOperation("createAgenda", request.getTitle(), true);

        Result<AgendaResponse> result = agendaService.createAgenda(request);

        logOperation("createAgenda", request.getTitle(), result.isSuccess());

        return handleCreateOperation(result, AgendaResponse::getId);
    }

    /**
     * Busca uma pauta pelo ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar pauta por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pauta encontrada"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    public ResponseEntity<AgendaResponse> getAgendaById(
            @PathVariable @ValidUUID(message = "ID deve ser um UUID válido") String id) {
        logOperation("getAgendaById", id, true);

        Result<AgendaResponse> result = agendaService.getAgendaById(id);

        logOperation("getAgendaById", id, result.isSuccess());

        return handleGetOperation(result);
    }

    /**
     * Lista todas as pautas com paginação simples
     */
    @GetMapping
    @Operation(summary = "Listar todas as pautas")
    @ApiResponse(responseCode = "200", description = "Lista de pautas retornada com sucesso")
    public ResponseEntity<PagedResponse<AgendaResponse>> getAllAgendas(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PagedResponse<AgendaResponse> agendas = agendaService.getAllAgendas(pageable);

        return ResponseEntity.ok(agendas);
    }

    /**
     * Lista pautas abertas
     */
    @GetMapping("/open")
    @Operation(summary = "Listar pautas abertas")
    @ApiResponse(responseCode = "200", description = "Lista de pautas abertas")
    public ResponseEntity<PagedResponse<AgendaResponse>> getOpenAgendas(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PagedResponse<AgendaResponse> agendas = agendaService.getAllAgendasWithOpenSessions(pageable);

        return ResponseEntity.ok(agendas);
    }

    /**
     * Lista pautas finalizadas
     */
    @GetMapping("/finished")
    @Operation(summary = "Listar pautas finalizadas")
    @ApiResponse(responseCode = "200", description = "Lista de pautas finalizadas")
    public ResponseEntity<PagedResponse<AgendaResponse>> getFinishedAgendas(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        PagedResponse<AgendaResponse> agendas = agendaService.getAllAgendasFinished(pageable);

        return ResponseEntity.ok(agendas);
    }
}