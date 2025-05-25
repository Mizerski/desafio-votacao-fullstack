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
import com.mizerski.backend.dtos.request.CreateVoteRequest;
import com.mizerski.backend.dtos.response.PagedResponse;
import com.mizerski.backend.dtos.response.VoteResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.services.IdempotencyService;
import com.mizerski.backend.services.VoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

/**
 * Controller para gerenciar operações relacionadas a votos.
 */
@RestController
@RequestMapping("/api/v1/votes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Votos", description = "Operações relacionadas ao sistema de votação")
public class VoteController extends BaseController {

    private final VoteService voteService;
    private final IdempotencyService idempotencyService;

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
            @PathVariable String userId,
            @PathVariable String agendaId) {

        try {
            VoteResponse vote = voteService.getVoteByUserIdAndAgendaId(userId, agendaId);
            return ResponseEntity.ok(vote);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista todos os votos de uma pauta
     */
    @GetMapping("/agenda/{agendaId}")
    @Operation(summary = "Listar votos por pauta")
    @ApiResponse(responseCode = "200", description = "Lista de votos da pauta")
    public ResponseEntity<PagedResponse<VoteResponse>> getVotesByAgenda(
            @PathVariable String agendaId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        // TODO: Implementar paginação no service
        var voteList = voteService.getAllVotesByAgendaId(agendaId);
        PagedResponse<VoteResponse> votes = new PagedResponse<>(voteList, page, size, voteList.size());

        return ResponseEntity.ok(votes);
    }

    /**
     * Lista todos os votos de um usuário
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar votos por usuário")
    @ApiResponse(responseCode = "200", description = "Lista de votos do usuário")
    public ResponseEntity<PagedResponse<VoteResponse>> getVotesByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {

        // TODO: Implementar paginação no service
        var voteList = voteService.getAllVotesByUserId(userId);
        PagedResponse<VoteResponse> votes = new PagedResponse<>(voteList, page, size, voteList.size());

        return ResponseEntity.ok(votes);
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

        try {
            // Validações básicas
            if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
                return Result.error("INVALID_USER", "ID do usuário é obrigatório");
            }

            if (request.getAgendaId() == null || request.getAgendaId().trim().isEmpty()) {
                return Result.error("INVALID_AGENDA", "ID da pauta é obrigatório");
            }

            if (request.getVoteType() == null) {
                return Result.error("INVALID_VOTE_TYPE", "Tipo de voto é obrigatório");
            }

            // Executa operação
            VoteResponse response = voteService.createVote(request);

            // Armazena no cache
            Result<VoteResponse> result = Result.success(response);
            idempotencyService.storeResult(idempotencyKey, response, 300);

            return result;

        } catch (Exception e) {
            // Mapeia exceptions
            String errorCode = switch (e.getClass().getSimpleName()) {
                case "NotFoundException" -> "AGENDA_NOT_FOUND";
                case "BadRequestException" ->
                    e.getMessage().contains("já votou") ? "USER_ALREADY_VOTED" : "AGENDA_NOT_OPEN";
                default -> "VOTE_ERROR";
            };

            return Result.error(errorCode, e.getMessage());
        }
    }
}