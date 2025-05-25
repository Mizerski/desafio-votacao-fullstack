package com.mizerski.backend.models.enums;

import org.springframework.http.HttpStatus;

/**
 * Enum que centraliza todos os códigos de erro da aplicação
 * com seus respectivos status HTTP e mensagens padrão.
 */
public enum ErrorCode {

    // Erros de validação (400 - Bad Request)
    INVALID_TITLE(HttpStatus.BAD_REQUEST, "Título inválido"),
    INVALID_DESCRIPTION(HttpStatus.BAD_REQUEST, "Descrição inválida"),
    INVALID_USER(HttpStatus.BAD_REQUEST, "Usuário inválido"),
    INVALID_AGENDA(HttpStatus.BAD_REQUEST, "Agenda inválida"),
    INVALID_VOTE_TYPE(HttpStatus.BAD_REQUEST, "Tipo de voto inválido"),

    // Erros de recurso não encontrado (404 - Not Found)
    AGENDA_NOT_FOUND(HttpStatus.NOT_FOUND, "Agenda não encontrada"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Usuário não encontrado"),
    VOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "Voto não encontrado"),

    // Erros de conflito (409 - Conflict)
    DUPLICATE_TITLE(HttpStatus.CONFLICT, "Título já existe"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "Email já cadastrado"),
    USER_ALREADY_VOTED(HttpStatus.CONFLICT, "Usuário já votou nesta agenda"),

    // Erros de regra de negócio (422 - Unprocessable Entity)
    AGENDA_NOT_OPEN(HttpStatus.UNPROCESSABLE_ENTITY, "Agenda não está aberta para votação"),
    OPERATION_NOT_ALLOWED(HttpStatus.UNPROCESSABLE_ENTITY, "Operação não permitida"),

    // Erros genéricos (500 - Internal Server Error)
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Erro desconhecido"),
    GENERIC_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");

    private final HttpStatus httpStatus;
    private final String defaultMessage;

    /**
     * Construtor do enum
     * 
     * @param httpStatus     Status HTTP correspondente ao erro
     * @param defaultMessage Mensagem padrão do erro
     */
    ErrorCode(HttpStatus httpStatus, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    /**
     * Obtém o status HTTP do erro
     * 
     * @return Status HTTP
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    /**
     * Obtém a mensagem padrão do erro
     * 
     * @return Mensagem padrão
     */
    public String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * Converte string para ErrorCode
     * 
     * @param code Código como string
     * @return ErrorCode correspondente ou UNKNOWN_ERROR se não encontrado
     */
    public static ErrorCode fromString(String code) {
        if (code == null || code.trim().isEmpty()) {
            return UNKNOWN_ERROR;
        }

        try {
            return ErrorCode.valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN_ERROR;
        }
    }

    /**
     * Verifica se o erro é do tipo "não encontrado"
     * 
     * @return true se for erro 404
     */
    public boolean isNotFound() {
        return httpStatus == HttpStatus.NOT_FOUND;
    }

    /**
     * Verifica se o erro é de validação
     * 
     * @return true se for erro 400
     */
    public boolean isBadRequest() {
        return httpStatus == HttpStatus.BAD_REQUEST;
    }

    /**
     * Verifica se o erro é de conflito
     * 
     * @return true se for erro 409
     */
    public boolean isConflict() {
        return httpStatus == HttpStatus.CONFLICT;
    }
}