package com.mizerski.backend.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mizerski.backend.models.domains.Result;

/**
 * Implementação do serviço responsável por mapear exceções para códigos de erro
 * Elimina a necessidade de switch cases para tratamento de exceções
 */
@Service
public class ExceptionMappingServiceImpl implements ExceptionMappingService {

    private final Map<String, String> exceptionToErrorCodeMap;

    /**
     * Construtor que inicializa o mapeamento de exceções para códigos de erro
     */
    public ExceptionMappingServiceImpl() {
        this.exceptionToErrorCodeMap = new HashMap<>();
        initializeExceptionMappings();
    }

    /**
     * Inicializa os mapeamentos de exceções para códigos de erro
     */
    private void initializeExceptionMappings() {
        // Exceções de recurso não encontrado
        exceptionToErrorCodeMap.put("NotFoundException", "AGENDA_NOT_FOUND");
        exceptionToErrorCodeMap.put("AgendaNotFoundException", "AGENDA_NOT_FOUND");
        exceptionToErrorCodeMap.put("UserNotFoundException", "USER_NOT_FOUND");
        exceptionToErrorCodeMap.put("VoteNotFoundException", "VOTE_NOT_FOUND");

        // Exceções de validação
        exceptionToErrorCodeMap.put("ValidationException", "INVALID_DATA");
        exceptionToErrorCodeMap.put("IllegalArgumentException", "INVALID_DATA");
        exceptionToErrorCodeMap.put("ConstraintViolationException", "INVALID_DATA");

        // Exceções de conflito
        exceptionToErrorCodeMap.put("ConflictException", "DUPLICATE_RESOURCE");
        exceptionToErrorCodeMap.put("DuplicateKeyException", "DUPLICATE_RESOURCE");
        exceptionToErrorCodeMap.put("DataIntegrityViolationException", "DUPLICATE_RESOURCE");

        // Exceções de regra de negócio
        exceptionToErrorCodeMap.put("BusinessRuleException", "OPERATION_NOT_ALLOWED");
        exceptionToErrorCodeMap.put("AgendaNotOpenException", "AGENDA_NOT_OPEN");

        // Exceções JWT
        exceptionToErrorCodeMap.put("ExpiredJwtException", "TOKEN_EXPIRED");
        exceptionToErrorCodeMap.put("MalformedJwtException", "INVALID_TOKEN");
        exceptionToErrorCodeMap.put("UnsupportedJwtException", "INVALID_TOKEN");
        exceptionToErrorCodeMap.put("SecurityException", "INVALID_TOKEN");
        exceptionToErrorCodeMap.put("JwtException", "INVALID_TOKEN");
    }

    /**
     * Mapeia uma exceção para um Result com código de erro apropriado
     * 
     * @param exception Exceção a ser mapeada
     * @return Result com erro mapeado
     */
    @Override
    public <T> Result<T> mapExceptionToResult(Exception exception) {
        String exceptionName = exception.getClass().getSimpleName();
        String errorCode = getErrorCodeForException(exceptionName, exception.getMessage());

        return Result.error(errorCode, exception.getMessage());
    }

    /**
     * Obtém o código de erro para uma exceção específica
     * 
     * @param exceptionName Nome da classe da exceção
     * @param message       Mensagem da exceção (para casos especiais)
     * @return Código de erro correspondente
     */
    private String getErrorCodeForException(String exceptionName, String message) {
        // Casos especiais baseados na mensagem
        if ("BadRequestException".equals(exceptionName)) {
            if (message != null && message.contains("já votou")) {
                return "USER_ALREADY_VOTED";
            }
            return "AGENDA_NOT_OPEN";
        }

        // Tratamento específico para violações de constraint de banco
        if ("DataIntegrityViolationException".equals(exceptionName) && message != null) {
            if (message.contains("email") || message.contains("UK_") && message.contains("email")) {
                return "DUPLICATE_EMAIL";
            }
            if (message.contains("document") || message.contains("UK_") && message.contains("document")) {
                return "DUPLICATE_DOCUMENT";
            }
            return "DUPLICATE_RESOURCE";
        }

        // Tratamento para violações de constraint de validação
        if ("ConstraintViolationException".equals(exceptionName) && message != null) {
            if (message.contains("email")) {
                return "INVALID_USER";
            }
            if (message.contains("name") || message.contains("nome")) {
                return "INVALID_USER";
            }
            if (message.contains("password") || message.contains("senha")) {
                return "INVALID_USER";
            }
            return "INVALID_DATA";
        }

        // Mapeamento padrão
        return exceptionToErrorCodeMap.getOrDefault(exceptionName, "UNKNOWN_ERROR");
    }

    /**
     * Adiciona um novo mapeamento de exceção para código de erro
     * 
     * @param exceptionName Nome da classe da exceção
     * @param errorCode     Código de erro correspondente
     */
    @Override
    public void addExceptionMapping(String exceptionName, String errorCode) {
        exceptionToErrorCodeMap.put(exceptionName, errorCode);
    }

    /**
     * Remove um mapeamento de exceção
     * 
     * @param exceptionName Nome da classe da exceção
     */
    @Override
    public void removeExceptionMapping(String exceptionName) {
        exceptionToErrorCodeMap.remove(exceptionName);
    }

    /**
     * Verifica se existe mapeamento para uma exceção
     * 
     * @param exceptionName Nome da classe da exceção
     * @return true se existe mapeamento
     */
    @Override
    public boolean hasMapping(String exceptionName) {
        return exceptionToErrorCodeMap.containsKey(exceptionName);
    }

    /**
     * Obtém todos os mapeamentos configurados
     * 
     * @return Map com todos os mapeamentos
     */
    @Override
    public Map<String, String> getAllMappings() {
        return new HashMap<>(exceptionToErrorCodeMap);
    }
}