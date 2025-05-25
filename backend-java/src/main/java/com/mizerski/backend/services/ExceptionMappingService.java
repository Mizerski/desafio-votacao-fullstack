package com.mizerski.backend.services;

import java.util.Map;

import com.mizerski.backend.models.domains.Result;

/**
 * Interface para serviço responsável por mapear exceções para códigos de erro
 * Elimina a necessidade de switch cases para tratamento de exceções
 */
public interface ExceptionMappingService {

    /**
     * Mapeia uma exceção para um Result com código de erro apropriado
     * 
     * @param exception Exceção a ser mapeada
     * @return Result com erro mapeado
     */
    <T> Result<T> mapExceptionToResult(Exception exception);

    /**
     * Adiciona um novo mapeamento de exceção para código de erro
     * 
     * @param exceptionName Nome da classe da exceção
     * @param errorCode     Código de erro correspondente
     */
    void addExceptionMapping(String exceptionName, String errorCode);

    /**
     * Remove um mapeamento de exceção
     * 
     * @param exceptionName Nome da classe da exceção
     */
    void removeExceptionMapping(String exceptionName);

    /**
     * Verifica se existe mapeamento para uma exceção
     * 
     * @param exceptionName Nome da classe da exceção
     * @return true se existe mapeamento
     */
    boolean hasMapping(String exceptionName);

    /**
     * Obtém todos os mapeamentos configurados
     * 
     * @return Map com todos os mapeamentos
     */
    Map<String, String> getAllMappings();
}