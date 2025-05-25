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

import com.mizerski.backend.annotations.Idempotent;
import com.mizerski.backend.dtos.request.CreateVoteRequest;
import com.mizerski.backend.dtos.response.VoteResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.services.IdempotencyService;
import com.mizerski.backend.services.VoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável por gerenciar operações relacionadas a votos.
 */
@RestController
@RequestMapping("/api/v1/votes")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Votos", description = "Operações relacionadas ao sistema de votação")
public class VoteController {

    private final VoteService voteService;
    private final IdempotencyService idempotencyService;

    /**
     * Registra um novo voto com tratamento de idempotência
     * 
     * @param request Dados do voto a ser registrado
     * @return ResponseEntity com dados do voto registrado ou erro
     */
    @PostMapping
    @Operation(summary = "Registrar voto", description = "Registra um voto em uma pauta com tratamento de idempotência")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou usuário já votou"),
            @ApiResponse(responseCode = "404", description = "Pauta não encontrada"),
            @ApiResponse(responseCode = "409", description = "Voto duplicado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> createVote(@Valid @RequestBody CreateVoteRequest request) {
        log.info("Recebida solicitação para registrar voto: usuário {} na pauta {}",
                request.getUserId(), request.getAgendaId());

        Result<VoteResponse> result = createVoteWithIdempotency(request);

        // Usa pattern matching do Result para retornar resposta apropriada
        if (result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(result.getValue().orElse(null));
        }

        // Mapeia códigos de erro para status HTTP apropriados
        return result.getErrorCode()
                .map(errorCode -> switch (errorCode) {
                    case "INVALID_USER", "INVALID_AGENDA", "INVALID_VOTE_TYPE" ->
                        ResponseEntity.badRequest()
                                .body(createErrorResponse(errorCode,
                                        result.getErrorMessage().orElse("Dados inválidos")));
                    case "AGENDA_NOT_FOUND" ->
                        ResponseEntity.notFound()
                                .build();
                    case "AGENDA_NOT_OPEN", "USER_ALREADY_VOTED" ->
                        ResponseEntity.badRequest()
                                .body(createErrorResponse(errorCode,
                                        result.getErrorMessage().orElse("Operação não permitida")));
                    case "DUPLICATE_VOTE" ->
                        ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(createErrorResponse(errorCode,
                                        result.getErrorMessage().orElse("Voto já registrado")));
                    default ->
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(createErrorResponse(errorCode,
                                        result.getErrorMessage().orElse("Erro interno")));
                })
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("UNKNOWN_ERROR", "Erro desconhecido")));
    }

    /**
     * Cria um voto com tratamento de idempotência
     * 
     * @param request Dados do voto
     * @return Result com o voto criado ou erro
     */
    @Idempotent(expireAfterSeconds = 300, includeUserId = true) // 5 minutos, incluindo userId
    private Result<VoteResponse> createVoteWithIdempotency(CreateVoteRequest request) {

        // Gera chave de idempotência baseada no usuário e pauta
        String idempotencyKey = idempotencyService.generateKey(
                "createVote",
                request.getUserId(),
                request.getAgendaId(),
                request.getVoteType().toString());

        // Verifica se operação já foi executada
        Result<VoteResponse> cachedResult = idempotencyService.checkIdempotency(idempotencyKey);
        if (cachedResult.isSuccess()) {
            log.info("Voto já registrado anteriormente, retornando resultado do cache");
            return cachedResult;
        }

        try {
            // Validações de negócio sem throws
            if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
                return Result.error("INVALID_USER", "ID do usuário é obrigatório");
            }

            if (request.getAgendaId() == null || request.getAgendaId().trim().isEmpty()) {
                return Result.error("INVALID_AGENDA", "ID da pauta é obrigatório");
            }

            if (request.getVoteType() == null) {
                return Result.error("INVALID_VOTE_TYPE", "Tipo de voto é obrigatório");
            }

            // Executa a operação de votação
            VoteResponse response = voteService.createVote(request);

            // Armazena resultado no cache de idempotência
            Result<VoteResponse> result = Result.success(response);
            idempotencyService.storeResult(idempotencyKey, response, 300);

            log.info("Voto registrado com sucesso: usuário {} votou {} na pauta {}",
                    request.getUserId(), request.getVoteType(), request.getAgendaId());
            return result;

        } catch (Exception e) {
            log.error("Erro ao registrar voto: {}", e.getMessage(), e);

            // Mapeia exceptions para códigos de erro específicos
            String errorCode = switch (e.getClass().getSimpleName()) {
                case "NotFoundException" -> "AGENDA_NOT_FOUND";
                case "BadRequestException" ->
                    e.getMessage().contains("já votou") ? "USER_ALREADY_VOTED" : "AGENDA_NOT_OPEN";
                default -> "VOTE_ERROR";
            };

            return Result.error(errorCode, e.getMessage());
        }
    }

    /**
     * Busca um voto específico por usuário e pauta
     * 
     * @param userId   ID do usuário
     * @param agendaId ID da pauta
     * @return ResponseEntity com dados do voto encontrado
     */
    @GetMapping("/user/{userId}/agenda/{agendaId}")
    @Operation(summary = "Buscar voto por usuário e pauta", description = "Busca um voto específico de um usuário em uma pauta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Voto encontrado"),
            @ApiResponse(responseCode = "404", description = "Voto não encontrado")
    })
    public ResponseEntity<VoteResponse> getVoteByUserAndAgenda(
            @PathVariable String userId,
            @PathVariable String agendaId) {
        log.debug("Buscando voto do usuário {} na pauta {}", userId, agendaId);

        try {
            VoteResponse vote = voteService.getVoteByUserIdAndAgendaId(userId, agendaId);
            return ResponseEntity.ok(vote);
        } catch (Exception e) {
            log.error("Erro ao buscar voto: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lista todos os votos de uma pauta
     * 
     * @param agendaId ID da pauta
     * @return ResponseEntity com lista de votos da pauta
     */
    @GetMapping("/agenda/{agendaId}")
    @Operation(summary = "Listar votos por pauta", description = "Retorna todos os votos registrados em uma pauta específica")
    @ApiResponse(responseCode = "200", description = "Lista de votos retornada com sucesso")
    public ResponseEntity<List<VoteResponse>> getVotesByAgenda(@PathVariable String agendaId) {
        log.debug("Listando votos da pauta {}", agendaId);

        List<VoteResponse> votes = voteService.getAllVotesByAgendaId(agendaId);
        return ResponseEntity.ok(votes);
    }

    /**
     * Lista todos os votos de um usuário
     * 
     * @param userId ID do usuário
     * @return ResponseEntity com lista de votos do usuário
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar votos por usuário", description = "Retorna todos os votos registrados por um usuário específico")
    @ApiResponse(responseCode = "200", description = "Lista de votos retornada com sucesso")
    public ResponseEntity<List<VoteResponse>> getVotesByUser(@PathVariable String userId) {
        log.debug("Listando votos do usuário {}", userId);

        List<VoteResponse> votes = voteService.getAllVotesByUserId(userId);
        return ResponseEntity.ok(votes);
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