package com.mizerski.backend.services;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.enums.ErrorCode;

/**
 * Implementação do serviço responsável por mapear códigos de erro para
 * ResponseEntity
 * Elimina a necessidade de switch cases nos controllers
 */
@Service
public class ErrorMappingServiceImpl implements ErrorMappingService {

    /**
     * Mapeia um Result com erro para ResponseEntity apropriado
     * 
     * @param result Result contendo erro
     * @return ResponseEntity com status e body adequados
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> ResponseEntity<T> mapErrorToResponse(Result<T> result) {
        if (result.isSuccess()) {
            throw new IllegalArgumentException("Result deve conter erro para ser mapeado");
        }

        String errorCodeString = result.getErrorCode().orElse("UNKNOWN_ERROR");
        ErrorCode errorCode = ErrorCode.fromString(errorCodeString);

        String errorMessage = result.getErrorMessage()
                .orElse(errorCode.getDefaultMessage());

        // Para erros 404, retorna apenas o status sem body
        if (errorCode.isNotFound()) {
            return (ResponseEntity<T>) ResponseEntity.status(errorCode.getHttpStatus()).build();
        }

        // Para outros erros, retorna com body de erro
        ErrorResponse errorResponse = new ErrorResponse(
                errorCodeString,
                errorMessage,
                LocalDateTime.now());

        return (ResponseEntity<T>) ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * Cria ResponseEntity de erro diretamente a partir de código e mensagem
     * 
     * @param errorCodeString Código do erro como string
     * @param customMessage   Mensagem customizada (opcional)
     * @return ResponseEntity com erro
     */
    @Override
    public ResponseEntity<ErrorResponse> createErrorResponse(String errorCodeString, String customMessage) {
        ErrorCode errorCode = ErrorCode.fromString(errorCodeString);

        String message = customMessage != null && !customMessage.trim().isEmpty()
                ? customMessage
                : errorCode.getDefaultMessage();

        ErrorResponse errorResponse = new ErrorResponse(
                errorCodeString,
                message,
                LocalDateTime.now());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    /**
     * Verifica se um código de erro específico deve retornar apenas status (sem
     * body)
     * 
     * @param errorCodeString Código do erro
     * @return true se deve retornar apenas status
     */
    @Override
    public boolean shouldReturnStatusOnly(String errorCodeString) {
        ErrorCode errorCode = ErrorCode.fromString(errorCodeString);
        return errorCode.isNotFound();
    }
}