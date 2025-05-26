# Sistema de Votação - Backend Java

Sistema backend robusto para gerenciamento de votações desenvolvido como teste técnico. Implementa arquitetura limpa, padrões de design modernos e otimizações de performance avançadas.

## 🚀 Começando

### Pré-requisitos

```bash
# Versões mínimas requeridas
java --version    # OpenJDK 17+
mvn --version     # Maven 3.8+
docker --version  # Docker 20+
```

### 🐳 Rodando com Docker

1. Clone o repositório
```bash
git clone [url-do-repositorio]
cd desafio-votacao-fullstack
```

2. Inicie os containers
```bash
docker-compose up -d
```

3. Verifique se os serviços estão rodando
```bash
docker-compose ps
```

### 📝 Criando sua Conta

1. Acesse o Swagger UI em: http://localhost:8080/swagger-ui/index.html

2. Vá até a seção "Autenticação" e procure pelo endpoint `/api/auth/register`

3. Crie uma conta usando o seguinte modelo:
```json
{
  "name": "Seu Nome",
  "email": "seu.email@exemplo.com",
  "password": "sua-senha-com-8-caracteres",
  "document": "12345678900"
}
```

4. Após criar a conta, faça login no endpoint `/api/auth/login` com seu email e senha

### 🔍 Documentação Detalhada

Para mais informações sobre a arquitetura e implementação, consulte:

- [Arquitetura e Padrões](docs/ARCHITECTURE.md)
- [Modelo de Dados](docs/DATABASE.md)
- [Padrões de Design](docs/PATTERNS.md)
- [Testes](docs/TESTS.md)
- [Deploy e Produção](docs/DEPLOY.md)
- [Métricas e Observabilidade](docs/METRICS.md)

## 🛠️ Desenvolvimento

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

```

## 📊 Monitoramento

- Health Check: http://localhost:8080/actuator/health
- Métricas: http://localhost:8080/actuator/metrics
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'feat: Adicionando nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

**Desenvolvido com 💙 por mizerski**
