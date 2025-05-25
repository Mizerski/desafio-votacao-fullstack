package com.mizerski.backend.services;

import com.mizerski.backend.models.domains.Result;

/**
 * Interface para serviço de gerenciamento de operações idempotentes.
 * Utiliza cache em memória para evitar operações duplicadas.
 */
public interface IdempotencyService {

    /**
     * Verifica se uma operação já foi executada
     * 
     * @param key Chave da operação
     * @return Result com o resultado anterior se existir
     */
    <T> Result<T> checkIdempotency(String key);

    /**
     * Armazena o resultado de uma operação no cache
     * 
     * @param key                Chave da operação
     * @param result             Resultado a ser armazenado
     * @param expireAfterSeconds Tempo de expiração em segundos
     */
    <T> void storeResult(String key, T result, int expireAfterSeconds);

    /**
     * Remove uma entrada específica do cache
     * 
     * @param key Chave a ser removida
     */
    void removeFromCache(String key);

    /**
     * Obtém estatísticas do cache
     * 
     * @return Informações sobre o estado atual do cache
     */
    String getCacheStats();

    /**
     * Gera uma chave de idempotência baseada em parâmetros
     * 
     * @param methodName Nome do método
     * @param params     Parâmetros do método
     * @return Chave única para a operação
     */
    String generateKey(String methodName, Object... params);
}