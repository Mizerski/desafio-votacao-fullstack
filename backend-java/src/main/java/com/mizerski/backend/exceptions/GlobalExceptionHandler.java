package com.mizerski.backend.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * Manipulador global de exceções para a API
 * Centraliza o tratamento de erros não capturados pelos serviços
 * e padroniza as respostas de erro
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Modelo padrão para respostas de erro
     */
    public static class ApiError {
        public final String message;
        public final int statusCode;
        public final String statusName;
        public final LocalDateTime timestamp;
        public final String path;

        /**
         * Construtor da resposta de erro
         * 
         * @param message    Mensagem de erro
         * @param statusCode Código HTTP do status
         * @param statusName Nome do status HTTP
         * @param timestamp  Timestamp do erro
         * @param path       Caminho da requisição que gerou o erro
         */
        public ApiError(String message, int statusCode, String statusName,
                LocalDateTime timestamp, String path) {
            this.message = message;
            this.statusCode = statusCode;
            this.statusName = statusName;
            this.timestamp = timestamp;
            this.path = path;
        }
    }

    /**
     * Trata exceções de validação de campos (@Valid)
     * Retorna um mapa com os campos inválidos e suas mensagens
     * 
     * @param ex      Exceção de validação
     * @param request Informações da requisição
     * @return ResponseEntity com mapa de erros de validação
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                "Dados de entrada inválidos",
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now(),
                extractPath(request),
                fieldErrors);

        log.warn("Erro de validação na requisição {}: {}", extractPath(request), fieldErrors);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Trata exceções gerais não capturadas pelos serviços
     * Este handler deve capturar apenas erros inesperados do sistema
     * 
     * @param ex      Exceção não tratada
     * @param request Informações da requisição
     * @return ResponseEntity com erro interno do servidor
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex, WebRequest request) {
        String path = extractPath(request);

        // Log detalhado para debugging
        log.error("Erro não tratado na requisição {}: {}", path, ex.getMessage(), ex);

        ApiError error = new ApiError(
                "Ocorreu um erro interno no servidor. Tente novamente mais tarde.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                LocalDateTime.now(),
                path);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Trata exceções de argumentos ilegais
     * Geralmente indica erro de programação ou dados inválidos
     * 
     * @param ex      Exceção de argumento ilegal
     * @param request Informações da requisição
     * @return ResponseEntity com erro de bad request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        String path = extractPath(request);

        log.warn("Argumento ilegal na requisição {}: {}", path, ex.getMessage());

        ApiError error = new ApiError(
                "Parâmetros inválidos fornecidos",
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                LocalDateTime.now(),
                path);

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Extrai o caminho da requisição das informações do WebRequest
     * 
     * @param request Informações da requisição
     * @return Caminho da requisição ou "unknown" se não disponível
     */
    private String extractPath(WebRequest request) {
        String path = request.getDescription(false);
        if (path != null && path.startsWith("uri=")) {
            return path.substring(4);
        }
        return "unknown";
    }

    /**
     * Classe para resposta de erro de validação
     * Estende ApiError para incluir detalhes dos campos inválidos
     */
    public static class ValidationErrorResponse extends ApiError {
        public final Map<String, String> fieldErrors;

        /**
         * Construtor da resposta de erro de validação
         * 
         * @param message     Mensagem geral do erro
         * @param statusCode  Código HTTP
         * @param statusName  Nome do status HTTP
         * @param timestamp   Timestamp do erro
         * @param path        Caminho da requisição
         * @param fieldErrors Mapa com erros específicos dos campos
         */
        public ValidationErrorResponse(String message, int statusCode, String statusName,
                LocalDateTime timestamp, String path, Map<String, String> fieldErrors) {
            super(message, statusCode, statusName, timestamp, path);
            this.fieldErrors = fieldErrors;
        }
    }
}