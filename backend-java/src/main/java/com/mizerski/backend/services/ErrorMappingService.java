package com.mizerski.backend.services;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import com.mizerski.backend.models.domains.Result;

/**
 * Interface para serviço responsável por mapear códigos de erro para
 * ResponseEntity
 * Elimina a necessidade de switch cases nos controllers
 */
public interface ErrorMappingService {

    /**
     * Mapeia um Result com erro para ResponseEntity apropriado
     * 
     * @param result Result contendo erro
     * @return ResponseEntity com status e body adequados
     */
    <T> ResponseEntity<T> mapErrorToResponse(Result<T> result);

    /**
     * Cria ResponseEntity de erro diretamente a partir de código e mensagem
     * 
     * @param errorCodeString Código do erro como string
     * @param customMessage   Mensagem customizada (opcional)
     * @return ResponseEntity com erro
     */
    ResponseEntity<ErrorResponse> createErrorResponse(String errorCodeString, String customMessage);

    /**
     * Verifica se um código de erro específico deve retornar apenas status (sem
     * body)
     * 
     * @param errorCodeString Código do erro
     * @return true se deve retornar apenas status
     */
    boolean shouldReturnStatusOnly(String errorCodeString);

    /**
     * Classe para resposta de erro padronizada
     */
    class ErrorResponse {
        public final String errorCode;
        public final String message;
        public final LocalDateTime timestamp;

        /**
         * Construtor da resposta de erro
         * 
         * @param errorCode Código do erro
         * @param message   Mensagem de erro
         * @param timestamp Timestamp do erro
         */
        public ErrorResponse(String errorCode, String message, LocalDateTime timestamp) {
            this.errorCode = errorCode;
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}