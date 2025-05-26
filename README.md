# Sistema de Votação - Fullstack

Sistema completo para gerenciamento de votações desenvolvido como teste técnico. Implementa arquitetura limpa, padrões de design modernos e otimizações de performance avançadas.

## 🚀 Começando

### Pré-requisitos

```bash
# Versões mínimas requeridas
java --version    # OpenJDK 17+
mvn --version     # Maven 3.8+
docker --version  # Docker 20+
node --version    # Node.js 18+
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

### 🌐 Acessando a Aplicação

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## 📚 Documentação

### Backend

Para mais informações sobre a implementação do backend, consulte:

- [Arquitetura e Padrões](backend-java/docs/ARCHITECTURE.md)
- [Modelo de Dados](backend-java/docs/DATABASE.md)
- [Padrões de Design](backend-java/docs/PATTERNS.md)
- [Testes](backend-java/docs/TESTS.md)
- [Deploy e Produção](backend-java/docs/DEPLOY.md)
- [Métricas e Observabilidade](backend-java/docs/METRICS.md)
- [Segurança](backend-java/docs/SECURITY.md)

### Frontend

Para mais informações sobre a implementação do frontend, consulte:

- [Arquitetura](web/docs/ARCHITECTURE.md)
- [Componentes](web/docs/COMPONENTS.md)
- [Estado Global](web/docs/STATE.md)
- [Testes](web/docs/TESTS.md)

## 🤝 Contribuindo

Por favor, leia o [Guia de Contribuição](CONTRIBUTING.md) para detalhes sobre nosso código de conduta e o processo para submeter pull requests.

## 📄 Licença

Este projeto está sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.
