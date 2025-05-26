# Deploy e Produção

## 1. Profile de Produção

```properties
# application-prod.properties
spring.profiles.active=prod

# Configurações de produção
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.root=WARN
logging.level.com.mizerski.backend=INFO

# Pool de conexões otimizado
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

## 2. Docker

```dockerfile
FROM openjdk:17-jre-slim

WORKDIR /app
COPY target/backend-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 3. Docker Compose

```yaml
version: '3.8'

services:
  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/backend_postgres
      - SPRING_DATASOURCE_USERNAME=backend_user
      - SPRING_DATASOURCE_PASSWORD=backend123
    depends_on:
      - db

  db:
    image: postgres:15-alpine
    ports:
      - "5432:5432"
    environment:
      - POSTGRES_DB=backend_postgres
      - POSTGRES_USER=backend_user
      - POSTGRES_PASSWORD=backend123
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

## 4. Monitoramento

```properties
# Actuator endpoints
management.endpoints.web.exposure.include=health,info,metrics,flyway
management.endpoint.health.show-details=always
```

## 5. Logs Estruturados

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="JSON_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeMDC>true</includeMDC>
            <includeContext>true</includeContext>
            <customFields>{"app":"backend-java"}</customFields>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="JSON_CONSOLE" />
    </root>
</configuration>
```

## 6. Health Checks

```http
GET /actuator/health
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "flyway": { "status": "UP" }
  }
}
```

## 7. Métricas

```http
GET /actuator/metrics
{
  "names": [
    "jvm.memory.used",
    "http.server.requests",
    "process.cpu.usage",
    "hikaricp.connections.active",
    "agenda.votes.total",
    "agenda.sessions.active"
  ]
}
```

## 8. Comandos de Deploy

```bash
# Build do projeto
./mvnw clean package -DskipTests

# Build da imagem Docker
docker build -t backend-java .

# Deploy com Docker Compose
docker-compose up -d

# Verificar logs
docker-compose logs -f backend

# Verificar status
docker-compose ps

# Escalar serviço
docker-compose up -d --scale backend=3
```

## 9. Checklist de Deploy

- [ ] Configurar profile de produção
- [ ] Configurar logs estruturados
- [ ] Habilitar métricas e monitoramento
- [ ] Configurar health checks
- [ ] Configurar backup do banco de dados
- [ ] Configurar limites de recursos (CPU/Memória)
- [ ] Configurar variáveis de ambiente
- [ ] Testar rollback
- [ ] Verificar segurança 