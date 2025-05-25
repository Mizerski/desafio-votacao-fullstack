package com.mizerski.backend.controllers;

import java.net.URI;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mizerski.backend.models.domains.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * Classe base para controllers que padroniza tratamento de erros.
 */
@Slf4j
public abstract class BaseController {

    /**
     * Cria ResponseEntity para operações de criação com Result pattern
     * 
     * @param result   Resultado da operação
     * @param idGetter Função para extrair ID do objeto criado
     * @return ResponseEntity com status 201 e Location header
     */
    protected <T> ResponseEntity<?> handleCreateOperation(Result<T> result,
            java.util.function.Function<T, String> idGetter) {
        if (result.isSuccess()) {
            T value = result.getValue().orElse(null);
            if (value != null) {
                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(idGetter.apply(value))
                        .toUri();

                return ResponseEntity.created(location).body(value);
            }
        }

        return handleErrorResponse(result);
    }

    /**
     * Cria ResponseEntity para operações de busca com Result pattern
     * 
     * @param result Resultado da operação
     * @return ResponseEntity com status apropriado
     */
    protected <T> ResponseEntity<T> handleGetOperation(Result<T> result) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue().orElse(null));
        }

        // Para operações GET, erro geralmente é 404
        return ResponseEntity.notFound().build();
    }

    /**
     * Cria ResponseEntity para operações de atualização com Result pattern
     * 
     * @param result Resultado da operação
     * @return ResponseEntity com status apropriado
     */
    protected <T> ResponseEntity<T> handleUpdateOperation(Result<T> result) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getValue().orElse(null));
        }

        return handleErrorResponse(result);
    }

    /**
     * Cria ResponseEntity para operações de deleção
     * 
     * @param success Se a operação foi bem-sucedida
     * @return ResponseEntity com status 204 (No Content) ou erro
     */
    protected ResponseEntity<Void> handleDeleteOperation(boolean success) {
        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Mapeia códigos de erro para status HTTP apropriados
     * 
     * @param result Resultado com erro
     * @return ResponseEntity com status e body de erro
     */
    @SuppressWarnings("unchecked")
    private <T> ResponseEntity<T> handleErrorResponse(Result<T> result) {
        return result.getErrorCode()
                .map(errorCode -> switch (errorCode) {
                    case "INVALID_TITLE", "INVALID_DESCRIPTION", "INVALID_USER",
                            "INVALID_AGENDA", "INVALID_VOTE_TYPE" ->
                        (ResponseEntity<T>) ResponseEntity.badRequest()
                                .body(createErrorResponse(errorCode,
                                        result.getErrorMessage().orElse("Dados inválidos")));

                    case "DUPLICATE_TITLE", "DUPLICATE_EMAIL", "USER_ALREADY_VOTED" ->
                        (ResponseEntity<T>) ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(createErrorResponse(errorCode,
                                        result.getErrorMessage().orElse("Recurso já existe")));

                    case "AGENDA_NOT_FOUND", "USER_NOT_FOUND", "VOTE_NOT_FOUND" ->
                        (ResponseEntity<T>) ResponseEntity.notFound()
                                .build();

                    case "AGENDA_NOT_OPEN", "OPERATION_NOT_ALLOWED" ->
                        (ResponseEntity<T>) ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                                .body(createErrorResponse(errorCode,
                                        result.getErrorMessage().orElse("Operação não permitida")));

                    default ->
                        (ResponseEntity<T>) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(createErrorResponse(errorCode,
                                        result.getErrorMessage().orElse("Erro interno do servidor")));
                })
                .orElse((ResponseEntity<T>) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(createErrorResponse("UNKNOWN_ERROR", "Erro desconhecido")));
    }

    /**
     * Cria uma resposta de erro padronizada
     * 
     * @param errorCode Código do erro
     * @param message   Mensagem de erro
     * @return Objeto de resposta de erro
     */
    @SuppressWarnings("unchecked")
    private <T> T createErrorResponse(String errorCode, String message) {
        return (T) new ErrorResponse(errorCode, message, LocalDateTime.now());
    }

    /**
     * Classe para resposta de erro padronizada
     */
    public static class ErrorResponse {
        public final String errorCode;
        public final String message;
        public final LocalDateTime timestamp;

        public ErrorResponse(String errorCode, String message, LocalDateTime timestamp) {
            this.errorCode = errorCode;
            this.message = message;
            this.timestamp = timestamp;
        }
    }

    /**
     * Log simples para operações
     */
    protected void logOperation(String operation, String identifier, boolean success) {
        if (success) {
            log.info("{} executado com sucesso: {}", operation, identifier);
        } else {
            log.warn("Falha em {}: {}", operation, identifier);
        }
    }

    /**
     * Log estruturado para operações de busca
     * 
     * @param operation  Nome da operação
     * @param identifier Identificador do recurso
     */
    protected void logQuery(String operation, String identifier) {
        log.debug("Executando {} para: {}", operation, identifier);
    }
}