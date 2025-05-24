

Arquitetura de pastas:

```
backend-java/
├── src/main/java/com/mizerski/backend/
│   ├── controller/          # = http/controllers
│   ├── service/            # = services  
│   ├── repository/         # = repositories
│   ├── model/entity/       # = models/entities
│   ├── dto/               # = validations/schemas
│   ├── config/            # = shared/middlewares
│   ├── util/              # = shared/utils
│   ├── exception/         # tratamento de erros
│   └── Application.java   # = index.ts
├── src/main/resources/
│   ├── application.yml    # = .env
│   └── data.sql          # seeds opcionais
└── pom.xml               # = package.json
```

Padrões e Convenções Spring Boot:
1. Nomenclatura (muito importante!):

- Controllers: UsuarioController.java
- Services: UsuarioService.java
- Repositories: UsuarioRepository.java
- Entities: Usuario.java
- DTOs: UsuarioDto.java ou CreateUsuarioRequest.java

Spring é code-first, então as entidades são criadas primeiro e depois as migrations são geradas, diferente do Prisma que é schema-first.

jpa, lombok, hibernate, annotations, jakarta,