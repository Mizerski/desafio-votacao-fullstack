package com.mizerski.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.mizerski.backend.services.SessionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuração do scheduler para processar sessões expiradas automaticamente
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SessionSchedulerConfig {

    private final SessionService sessionService;

    /**
     * Processa sessões expiradas a cada 30 segundos
     * Finaliza automaticamente agendas cujas sessões expiraram
     */
    @Scheduled(fixedRate = 30000) // 30 segundos
    public void processExpiredSessions() {
        try {
            int processedCount = sessionService.processExpiredSessions();

            if (processedCount > 0) {
                log.info("Scheduler: {} sessões expiradas processadas", processedCount);
            }

        } catch (Exception e) {
            log.error("Erro no scheduler de sessões expiradas: {}", e.getMessage(), e);
        }
    }
}