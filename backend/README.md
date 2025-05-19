# Backend

## Tecnologias usadas no projeto e versões

```json
{
  "node": "v18.20.5",
  "fastify": "v5.0.0",
  "typescript": "v5.6.3",
  "zod": "v3.23.8",
  "prisma": "v5.20.0",
  "socket.io": "v4.8.1",
  "jwt": "v9.0.2"
}
```

## Como rodar este projeto

1 - Instale as dependências do projeto:

```bash
bun install
```

2 - Crie o banco de dados:

```bash
docker compose up -d
```

3 - Rode as migrations:

```bash
bun prisma migrate dev
```

4 - Rode o seed:

```bash
bun prisma db seed
```

4 - Rode o projeto:

```bash
bun run dev
```







