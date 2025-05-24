# Backend Java

### Tecnologias utilizadas

Projeto backend robusto, moderno e pronto para produção. As versões recomendadas para garantir compatibilidade são:

```json
{
  "java": "17",
  "spring-boot": "3.5.0",
  "postgresql": ">=42.2.0",
  "lombok": "1.18.38"
}
```

> Recomenda-se utilizar as versões acima para evitar problemas de build ou incompatibilidades.

---

### Arquitetura e organização

O projeto segue uma arquitetura limpa, separando responsabilidades em camadas bem definidas para facilitar manutenção, testes e escalabilidade.

#### Estrutura de pastas

```
backend-java/
├── src/main/java/com/mizerski/backend/
│   ├── controller/          # Controllers HTTP (entrada das requisições)
│   ├── service/             # Regras de negócio e orquestração
│   ├── repository/          # Acesso a dados (JPA)
│   ├── model/entity/        # Entidades do domínio (JPA)
│   ├── dto/                 # Objetos de transferência de dados (Request/Response)
│   ├── config/              # Configurações globais (CORS, segurança, etc)
│   ├── util/                # Utilitários e helpers
│   ├── exception/           # Tratamento centralizado de erros
│   └── Application.java     # Ponto de entrada da aplicação
├── src/main/resources/
│   ├── application.properties         # Configurações (banco, portas, etc)
│   ├── application.properties.example # Exemplo de configuração
│   └── data.sql                      # Seeds opcionais para o banco
└── pom.xml                           # Gerenciador de dependências Maven
```

---

### Principais tecnologias e padrões

- **Spring Boot 3.5**: Framework principal para APIs REST, injeção de dependências e configuração simplificada.
- **Spring Data JPA**: Abstração para persistência de dados com o banco relacional.
- **PostgreSQL**: Banco de dados relacional robusto e open source.
- **Lombok**: Reduz boilerplate com anotações para getters, setters, construtores, etc.
- **Bean Validation (Jakarta Validation)**: Validação automática de dados de entrada via anotações.
- **Maven**: Gerenciamento de dependências e build.

---

### Fluxos principais

1. **Autenticação**: (implementar conforme necessário) – pode ser JWT, OAuth2, etc.
2. **Gestão de pautas**: Criação, listagem e detalhamento de pautas para votação.
3. **Processo de votação**: Abertura de sessões, registro de votos e apuração.
4. **Resultados**: Exibição de resultados em tempo real e histórico.

---

### Como rodar o projeto localmente

#### 1. Pré-requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL rodando (ou Docker)
- (Opcional) Docker Compose para facilitar o setup do banco

#### 2. Configurar variáveis de ambiente

Copie o arquivo de exemplo e ajuste conforme seu ambiente:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Edite o arquivo `src/main/resources/application.properties` com as credenciais do seu banco PostgreSQL:

```properties
spring.application.name=backend

# Configuração do banco de dados
spring.datasource.url=jdbc:postgresql://localhost:5433/backend_postgres
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
spring.datasource.driver-class-name=org.postgresql.Driver
```

#### 3. Instalar dependências e compilar

```bash
mvn clean install
```

#### 4. Rodar a aplicação

```bash
mvn spring-boot:run
```

A API estará disponível em: [http://localhost:8080](http://localhost:8080)

---

### Convenções e dicas de código

- **Documentação**: Sempre documente métodos, classes e funções utilitárias usando Javadoc em português.
- **Padrão de camadas**: Controllers não devem conter lógica de negócio, apenas orquestrar chamadas para services.
- **Validação**: Use anotações de validação nos DTOs para garantir integridade dos dados.
- **Tratamento de erros**: Centralize o tratamento de exceções na camada `exception/` para respostas padronizadas.
- **Testes**: Utilize o Spring Boot Test para criar testes unitários e de integração.

---

### Exemplo de endpoint

```java
@RestController
@RequestMapping("/api/pautas")
public class PautaController {

    private final PautaService pautaService;

    public PautaController(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    /**
     * Lista todas as pautas cadastradas.
     * @return Lista de pautas
     */
    @GetMapping
    public ResponseEntity<List<PautaResponse>> listarPautas() {
        return ResponseEntity.ok(pautaService.listarTodas());
    }
}
```

---

### Contribuindo

- Siga o padrão de commits: `<tipo>: título resumido da alteração`
- Sempre documente métodos, classes e funções auxiliares.
- Abra issues para bugs, sugestões ou melhorias.
- Testes são bem-vindos e ajudam a manter a qualidade do projeto.

---

Desenvolvido com 💙 por mizerski
