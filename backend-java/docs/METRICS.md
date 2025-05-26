# Métricas e Observabilidade

## 1. Logs Estruturados

### Configuração Logback
```xml
<!-- logback-spring.xml -->
<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] [correlationId=%X{correlationId}] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <root level="INFO">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>
```

### Configuração no application.yml
```yaml
# Configurações de logging
logging:
  level:
    com.mizerski.backend: INFO
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

## 2. Métricas do Sistema

### Configuração do Actuator
```yaml
# Configurações do Actuator
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
```

### Endpoints Disponíveis
```http
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```

## 3. Logs de Operações

### Logs de Autenticação
```java
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(...) {
        log.debug("Usuário autenticado: {}", userEmail);
        log.warn("Token JWT inválido para usuário: {}", userEmail);
    }
}
```

### Logs de Operações CRUD
```java
@Slf4j
public abstract class BaseController {
    protected void logOperation(String operation, String identifier, boolean success) {
        if (success) {
            log.info("{} executado com sucesso: {}", operation, identifier);
        } else {
            log.warn("Falha em {}: {}", operation, identifier);
        }
    }
}
```

## 4. Cache e Idempotência

### Métricas de Cache
```java
@Service
@Slf4j
public class IdempotencyServiceImpl implements IdempotencyService {
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        // Limpa cache expirado a cada 5 minutos
        scheduler.scheduleAtFixedRate(this::cleanExpiredEntries, 5, 5, TimeUnit.MINUTES);
        log.info("IdempotencyService iniciado com limpeza automática de cache");
    }
    
    public String getCacheStats() {
        return String.format("Cache: %d entradas ativas", cache.size());
    }
}
```

## 5. Monitoramento de Sessões

### Scheduler de Sessões
```java
@Configuration
@EnableScheduling
@Slf4j
public class SessionSchedulerConfig {
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
```

## 6. Checklist de Observabilidade

### ✅ Implementado
- [x] Logs estruturados com Logback
- [x] Métricas básicas via Actuator (health, info, metrics)
- [x] Logs de operações CRUD
- [x] Logs de autenticação e segurança
- [x] Monitoramento de cache e idempotência
- [x] Scheduler para processamento de sessões
- [x] Tratamento centralizado de exceções

