package com.mizerski.backend.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.mizerski.backend.models.domains.Result;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço para gerenciar operações idempotentes.
 * Utiliza cache em memória para evitar operações duplicadas.
 */
@Service
@Slf4j
public class IdempotencyService {

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Entrada do cache com resultado e timestamp
     */
    private static class CacheEntry {
        final Object result;
        final LocalDateTime timestamp;
        final int expireAfterSeconds;

        CacheEntry(Object result, int expireAfterSeconds) {
            this.result = result;
            this.timestamp = LocalDateTime.now();
            this.expireAfterSeconds = expireAfterSeconds;
        }

        boolean isExpired() {
            return ChronoUnit.SECONDS.between(timestamp, LocalDateTime.now()) > expireAfterSeconds;
        }
    }

    @PostConstruct
    public void init() {
        // Limpa cache expirado a cada 5 minutos
        scheduler.scheduleAtFixedRate(this::cleanExpiredEntries, 5, 5, TimeUnit.MINUTES);
        log.info("IdempotencyService iniciado com limpeza automática de cache");
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
        log.info("IdempotencyService finalizado");
    }

    /**
     * Verifica se uma operação já foi executada
     * 
     * @param key Chave da operação
     * @return Result com o resultado anterior se existir
     */
    @SuppressWarnings("unchecked")
    public <T> Result<T> checkIdempotency(String key) {
        CacheEntry entry = cache.get(key);

        if (entry == null) {
            return Result.error("NOT_FOUND", "Operação não encontrada no cache");
        }

        if (entry.isExpired()) {
            cache.remove(key);
            return Result.error("EXPIRED", "Operação expirada no cache");
        }

        log.debug("Operação idempotente encontrada para chave: {}", key);
        return Result.success((T) entry.result);
    }

    /**
     * Armazena o resultado de uma operação no cache
     * 
     * @param key                Chave da operação
     * @param result             Resultado a ser armazenado
     * @param expireAfterSeconds Tempo de expiração em segundos
     */
    public <T> void storeResult(String key, T result, int expireAfterSeconds) {
        cache.put(key, new CacheEntry(result, expireAfterSeconds));
        log.debug("Resultado armazenado no cache para chave: {}", key);
    }

    /**
     * Remove uma entrada específica do cache
     * 
     * @param key Chave a ser removida
     */
    public void removeFromCache(String key) {
        cache.remove(key);
        log.debug("Entrada removida do cache: {}", key);
    }

    /**
     * Limpa todas as entradas expiradas do cache
     */
    private void cleanExpiredEntries() {
        int removedCount = 0;
        var iterator = cache.entrySet().iterator();

        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue().isExpired()) {
                iterator.remove();
                removedCount++;
            }
        }

        if (removedCount > 0) {
            log.debug("Limpeza de cache: {} entradas expiradas removidas", removedCount);
        }
    }

    /**
     * Obtém estatísticas do cache
     * 
     * @return Informações sobre o estado atual do cache
     */
    public String getCacheStats() {
        return String.format("Cache: %d entradas ativas", cache.size());
    }

    /**
     * Gera uma chave de idempotência baseada em parâmetros
     * 
     * @param methodName Nome do método
     * @param params     Parâmetros do método
     * @return Chave única para a operação
     */
    public String generateKey(String methodName, Object... params) {
        StringBuilder keyBuilder = new StringBuilder(methodName);

        for (Object param : params) {
            keyBuilder.append(":")
                    .append(param != null ? param.toString() : "null");
        }

        return keyBuilder.toString();
    }
}