# Sistema de Votação - Backend Java

Este é um guia passo a passo para executar o backend do Sistema de Votação, desenvolvido como teste técnico.

## Importante: Problemas Conhecidos e Soluções

Ao rodar o projeto pela primeira vez após clonar, você pode encontrar o seguinte erro:

```
Migration checksum mismatch for migration version 3
-> Applied to database : 356241697
-> Resolved locally    : -1833994198
```

**Solução**: Execute o comando de reparo do Flyway:
```bash
./mvnw flyway:repair
```

##  Passo a Passo para Execução

### 1. Pré-requisitos

```bash
# Versões mínimas requeridas
java --version    # OpenJDK 17+
mvn --version     # Maven 3.8+
docker --version  # Docker 20+
```

### 2. Iniciando a Aplicação

```bash
# 1. Inicie o banco de dados com Docker
docker-compose up -d

# 2. Verifique se o container está rodando
docker-compose ps

# 3. Execute o reparo do Flyway (caso necessário)
./mvnw flyway:repair

# 4. Inicie a aplicação
./mvnw spring-boot:run
```

A aplicação estará disponível em: http://localhost:8080

### 3. Endpoints Principais

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Health Check: http://localhost:8080/actuator/health
- API Base: http://localhost:8080/api/v1

### 4. Criando sua Conta de Teste

Use o Swagger UI (http://localhost:8080/swagger-ui/index.html) para:

1. Registrar um usuário:
```json
POST /api/auth/register
{
  "name": "Usuário Teste",
  "email": "teste@exemplo.com",
  "password": "senha123",
  "document": "12345678900"
}
```

2. Fazer login:
```json
POST /api/auth/login
{
  "email": "teste@exemplo.com",
  "password": "senha123"
}
```

## Troubleshooting

### Problema 1: Erro de Migração Flyway
**Sintoma**: Erro "Migration checksum mismatch"
**Solução**: Execute `./mvnw flyway:repair`

### Problema 2: Erro de Conexão com Banco
**Sintoma**: Não consegue conectar ao PostgreSQL
**Solução**: Verifique se o container está rodando com `docker-compose ps`

### Problema 3: Porta 8080 em Uso
**Sintoma**: Erro "Port 8080 already in use"
**Solução**: Pare outros serviços usando a porta 8080 ou altere a porta no `application.properties`



### Estrutura do Projeto

```
src/main/java/com/mizerski/backend/
├── annotations/      # Anotações customizadas
├── config/          # Configurações Spring
├── controllers/     # Controllers REST
├── dtos/           # DTOs (Request/Response)
├── exceptions/     # Exceções customizadas
├── models/         # Entidades e domínios
├── repositories/   # Repositórios JPA
└── services/       # Serviços de negócio
```

### Comandos Úteis

```bash
# Compilar o projeto
./mvnw clean install

# Rodar testes
./mvnw test

# Rodar localmente
./mvnw spring-boot:run

# Rodar o projeto com o Flyway
./mvnw flyway:migrate

# Reparar o Flyway
./mvnw flyway:repair

```

## Monitoramento

- Health Check: http://localhost:8080/actuator/health
- Métricas: http://localhost:8080/actuator/metrics
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## Segurança

- Todas as senhas são criptografadas com BCrypt
- Autenticação via JWT
- CORS configurado para desenvolvimento local
- Endpoints públicos e privados devidamente configurados

## 📚 Documentação Detalhada

Para mais informações sobre a arquitetura e implementação, consulte:

- [Arquitetura e Padrões](docs/ARCHITECTURE.md)
- [Modelo de Dados](docs/DATABASE.md)
- [Padrões de Design](docs/PATTERNS.md)
- [Testes](docs/TESTS.md)
- [Deploy e Produção](docs/DEPLOY.md)
- [Métricas e Observabilidade](docs/METRICS.md)


**Desenvolvido com 💙 por mizerski**
