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

import com.mizerski.backend.annotations.Idempotent;
import com.mizerski.backend.annotations.ValidUUID;
import com.mizerski.backend.dtos.request.CreateVoteRequest;
import com.mizerski.backend.dtos.response.PagedResponse;
import com.mizerski.backend.dtos.response.VoteResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.services.ErrorMappingService;
import com.mizerski.backend.services.IdempotencyService;
import com.mizerski.backend.services.VoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Controller para gerenciar operações relacionadas a votos.
 */
@RestController
@RequestMapping("/api/v1/votes")
@Validated
@Tag(name = "Votos", description = "Operações relacionadas ao sistema de votação")
public class VoteController extends BaseController {

    private final VoteService voteService;
    private final IdempotencyService idempotencyService;

    /**
     * Construtor para injeção de dependência via construtor
     * 
     * @param errorMappingService Serviço de mapeamento de erros
     * @param voteService         Serviço de votos
     * @param idempotencyService  Serviço de idempotência
     */
    public VoteController(ErrorMappingService errorMappingService, VoteService voteService,
            IdempotencyService idempotencyService) {
        super(errorMappingService);
        this.voteService = voteService;
        this.idempotencyService = idempotencyService;
    }

    /**
     * Registra um novo voto com tratamento de idempotência
     */
    @PostMapping
    @Operation(summary = "Registrar voto", description = "Registra um voto em uma pauta com tratamento de idempotência")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
            @ApiResponse(responseCode = "409", description = "Usuário já votou")
    })
    public ResponseEntity<?> createVote(@Valid @RequestBody CreateVoteRequest request) {
        logOperation("createVote",
                String.format("user=%s, agenda=%s", request.getUserId(), request.getAgendaId()),
                true);

        Result<VoteResponse> result = createVoteWithIdempotency(request);

        logOperation("createVote",
                String.format("user=%s, agenda=%s", request.getUserId(), request.getAgendaId()),
                result.isSuccess());

        return handleCreateOperation(result, vote -> vote.getId());
    }

    /**
     * Busca um voto específico por usuário e pauta
     */
    @GetMapping("/user/{userId}/agenda/{agendaId}")
    @Operation(summary = "Buscar voto por usuário e pauta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voto encontrado"),
            @ApiResponse(responseCode = "404", description = "Voto não encontrado")
    })
    public ResponseEntity<VoteResponse> getVoteByUserAndAgenda(
            @PathVariable @ValidUUID(message = "ID do usuário deve ser um UUID válido") String userId,
            @PathVariable @ValidUUID(message = "ID da agenda deve ser um UUID válido") String agendaId) {

        Result<VoteResponse> result = voteService.getVoteByUserIdAndAgendaId(userId, agendaId);

        return handleGetOperation(result);
    }

    /**
     * Lista todos os votos de uma pauta
     */
    @GetMapping("/agenda/{agendaId}")
    @Operation(summary = "Listar votos por pauta")
    @ApiResponse(responseCode = "200", description = "Lista de votos da pauta")
    public ResponseEntity<PagedResponse<VoteResponse>> getVotesByAgenda(
            @PathVariable @ValidUUID(message = "ID da agenda deve ser um UUID válido") String agendaId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        Result<PagedResponse<VoteResponse>> result = voteService.getAllVotesByAgendaId(agendaId,
                org.springframework.data.domain.PageRequest.of(page, size));

        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue().orElse(null));
        }

        return errorMappingService.mapErrorToResponse(result);
    }

    /**
     * Lista todos os votos de um usuário
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar votos por usuário")
    @ApiResponse(responseCode = "200", description = "Lista de votos do usuário")
    public ResponseEntity<PagedResponse<VoteResponse>> getVotesByUser(
            @PathVariable @ValidUUID(message = "ID do usuário deve ser um UUID válido") String userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        Result<PagedResponse<VoteResponse>> result = voteService.getAllVotesByUserId(userId,
                org.springframework.data.domain.PageRequest.of(page, size));

        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue().orElse(null));
        }

        return errorMappingService.mapErrorToResponse(result);
    }

    /**
     * Cria um voto com tratamento de idempotência
     */
    @Idempotent(expireAfterSeconds = 300, includeUserId = true)
    private Result<VoteResponse> createVoteWithIdempotency(CreateVoteRequest request) {

        // Gera chave de idempotência
        String idempotencyKey = idempotencyService.generateKey(
                "createVote",
                request.getUserId(),
                request.getAgendaId(),
                request.getVoteType().toString());

        // Verifica cache
        Result<VoteResponse> cachedResult = idempotencyService.checkIdempotency(idempotencyKey);
        if (cachedResult.isSuccess()) {
            return cachedResult;
        }

        // Executa operação usando Result Pattern
        Result<VoteResponse> result = voteService.createVote(request);

        // Armazena no cache apenas se sucesso
        if (result.isSuccess()) {
            idempotencyService.storeResult(idempotencyKey, result.getValue().orElse(null), 300);
        }

        return result;
    }
}