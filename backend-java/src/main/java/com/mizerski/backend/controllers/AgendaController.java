package com.mizerski.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mizerski.backend.dtos.request.CreateAgendaRequest;
import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.dtos.response.PagedResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.services.AgendaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

/**
 * Controller para gerenciar operações relacionadas a pautas.
 */
@RestController
@RequestMapping("/api/v1/agendas")
@RequiredArgsConstructor
@Validated
@Tag(name = "Agendas", description = "Operações relacionadas a pautas de votação")
public class AgendaController extends BaseController {

    private final AgendaService agendaService;

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
    public ResponseEntity<AgendaResponse> getAgendaById(@PathVariable String id) {
        try {
            AgendaResponse agenda = agendaService.getAgendaById(id);
            return ResponseEntity.ok(agenda);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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

        // TODO: Implementar paginação no service
        var agendaList = agendaService.getAllAgendas();
        PagedResponse<AgendaResponse> agendas = new PagedResponse<>(agendaList, page, size, agendaList.size());

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

        // TODO: Implementar paginação no service
        var agendaList = agendaService.getAllAgendasWithOpenSessions();
        PagedResponse<AgendaResponse> agendas = new PagedResponse<>(agendaList, page, size, agendaList.size());

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

        // TODO: Implementar paginação no service
        var agendaList = agendaService.getAllAgendasFinished();
        PagedResponse<AgendaResponse> agendas = new PagedResponse<>(agendaList, page, size, agendaList.size());

        return ResponseEntity.ok(agendas);
    }
}