package com.mizerski.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mizerski.backend.dtos.request.CreateAgendaRequest;
import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.services.AgendaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável por gerenciar operações relacionadas a pautas.
 * Demonstra o uso de idempotência e Result pattern para melhor performance.
 */
@RestController
@RequestMapping("/api/v1/agendas")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Agendas", description = "Operações relacionadas a pautas de votação")
public class AgendaController {

    private final AgendaService agendaService;

    /**
     * Cria uma nova pauta com tratamento de idempotência
     * 
     * @param request Dados da pauta a ser criada
     * @return ResponseEntity com dados da pauta criada ou erro
     */
    @PostMapping
    @Operation(summary = "Criar nova pauta", description = "Cria uma nova pauta com tratamento de idempotência para evitar duplicatas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Pauta já existe"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> createAgenda(@Valid @RequestBody CreateAgendaRequest request) {
        log.info("Recebida solicitação para criar pauta: {}", request.getTitle());

        Result<AgendaResponse> result = agendaService.createAgenda(request);

        // Usa pattern matching do Result para retornar resposta apropriada
        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(result.getValue().orElse(null));
        }

        // Mapeia códigos de erro para status HTTP apropriados
        return result.getErrorCode()
                .map(errorCode -> switch (errorCode) {
                    case "INVALID_TITLE", "INVALID_DESCRIPTION" ->
                        ResponseEntity.badRequest()
                                .body(createErrorResponse(errorCode,
                                        result.getErrorMessage().orElse("Dados inválidos")));
                    case "DUPLICATE_TITLE" ->
                        ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(createErrorResponse(errorCode,
                                        result.getErrorMessage().orElse("Pauta já existe")));
                    default ->
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(createErrorResponse(errorCode, result.getErrorMessage().orElse("Erro interno")));
                })
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("UNKNOWN_ERROR", "Erro desconhecido")));
    }

    /**
     * Busca uma pauta pelo ID
     * 
     * @param id ID da pauta
     * @return ResponseEntity com dados da pauta encontrada
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar pauta por ID", description = "Busca uma pauta específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pauta encontrada"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    })
    public ResponseEntity<AgendaResponse> getAgendaById(@PathVariable String id) {
        log.debug("Buscando pauta com ID: {}", id);

        try {
            AgendaResponse agenda = agendaService.getAgendaById(id);
            return ResponseEntity.ok(agenda);
        } catch (Exception e) {
            log.error("Erro ao buscar pauta: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista todas as pautas
     * 
     * @return ResponseEntity com lista de pautas
     */
    @GetMapping
    @Operation(summary = "Listar todas as pautas", description = "Retorna uma lista com todas as pautas cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de pautas retornada com sucesso")
    public ResponseEntity<List<AgendaResponse>> getAllAgendas() {
        log.debug("Listando todas as pautas");

        List<AgendaResponse> agendas = agendaService.getAllAgendas();
        return ResponseEntity.ok(agendas);
    }

    /**
     * Lista pautas com sessões abertas
     * 
     * @return ResponseEntity com lista de pautas abertas
     */
    @GetMapping("/open")
    @Operation(summary = "Listar pautas abertas", description = "Retorna pautas que estão abertas para votação")
    @ApiResponse(responseCode = "200", description = "Lista de pautas abertas retornada com sucesso")
    public ResponseEntity<List<AgendaResponse>> getOpenAgendas() {
        log.debug("Listando pautas abertas");

        List<AgendaResponse> agendas = agendaService.getAllAgendasWithOpenSessions();
        return ResponseEntity.ok(agendas);
    }

    /**
     * Lista pautas finalizadas
     * 
     * @return ResponseEntity com lista de pautas finalizadas
     */
    @GetMapping("/finished")
    @Operation(summary = "Listar pautas finalizadas", description = "Retorna pautas que já foram finalizadas")
    @ApiResponse(responseCode = "200", description = "Lista de pautas finalizadas retornada com sucesso")
    public ResponseEntity<List<AgendaResponse>> getFinishedAgendas() {
        log.debug("Listando pautas finalizadas");

        List<AgendaResponse> agendas = agendaService.getAllAgendasFinished();
        return ResponseEntity.ok(agendas);
    }

    /**
     * Cria uma resposta de erro padronizada
     * 
     * @param errorCode Código do erro
     * @param message   Mensagem de erro
     * @return Objeto de resposta de erro
     */
    private ErrorResponse createErrorResponse(String errorCode, String message) {
        return new ErrorResponse(errorCode, message, System.currentTimeMillis());
    }

    /**
     * Classe para resposta de erro padronizada
     */
    public static class ErrorResponse {
        public final String errorCode;
        public final String message;
        public final long timestamp;

        public ErrorResponse(String errorCode, String message, long timestamp) {
            this.errorCode = errorCode;
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}