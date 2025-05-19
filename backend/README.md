# Backend

### Tecnologias que usei por aqui

Pra deixar tudo redondo e moderno, essas são as versões das ferramentas que usei no projeto:

```json
{
  "node": "v18.20.5",
  "fastify": "v5.0.0",
  "typescript": "v5.6.3",
  "zod": "v3.23.8",
  "prisma": "v5.20.0",
  "jwt": "v9.0.2"
}
```

> Se estiver usando uma versão muito diferente de alguma dessas, pode acabar tendo problema. Fica o aviso.

---

### Arquitetura técnica

Esse projeto foi estruturado seguindo alguns padrões e usando ferramentas modernas pra garantir código limpo e escalável:

#### Validação e contratos

Uso do **Zod** para validação de dados de entrada. Isso garante que tudo que chega na API está no formato esperado antes mesmo de ir pro banco.

```typescript
// Exemplo de schema com Zod
export const agendaBodySchema = z.object({
  title: z.string().min(3, 'Título inválido!').max(100),
  description: z.string().min(3, 'Descrição inválida!').max(255),
  // ...outros campos
})
```

#### Pattern de Repositórios

Implementei o pattern de repositórios pra isolar a lógica de acesso a dados. Facilita nos testes e deixa o código mais organizado.

```typescript
// Exemplo simplificado de um repository
export class AgendaRepository {
  async findById(id: string) {
    return prisma.agenda.findUnique({ where: { id } })
  }
  // ...outros métodos
}
```

#### ORM com Prisma

O **Prisma** cuida de toda interação com o banco, oferecendo type-safety e facilitando migrations.

#### Autenticação com JWT

Sistema de autenticação baseado em tokens JWT, com refresh token implementado.

---

Essa estrutura deixa o código mais testável, organizado e fácil de manter conforme o projeto cresce. E o melhor: tudo tipado com TypeScript pra evitar surpresas em produção.

---

### Subindo o projeto localmente

Seguindo os passos abaixo, em poucos minutos você já deve ter o projeto rodando na sua máquina.

---

#### 1. Instalar as dependências

Simples assim:

```bash
bun install
```

> Se você não tem o Bun instalado, [dá uma olhada aqui](https://bun.sh/docs/installation).

---

#### 2. Subir o banco de dados

A gente usa Docker pra facilitar a vida. Basta rodar:

```bash
docker compose up -d
```

> Se você nunca usou Docker, talvez seja bom dar uma lida rápida na [documentação oficial](https://docs.docker.com/).

---

#### 3. Rodar as migrations

Isso vai criar as tabelas e estrutura do banco com base no schema do Prisma:

```bash
bun prisma migrate dev
```

---

#### 4. Popular o banco com dados iniciais (seeds)

Pra não começar do zero:

```bash
bun prisma db seed
```

---

#### 5. Configurar as variáveis de ambiente

Aqui não tem segredo. Copie o arquivo `.env.example` e configure o seu `.env` com os dados corretos:

```bash
cp .env.example .env
```

E preencha mais ou menos assim:

```env
POSTGRES_PASSWORD=exemplo123
POSTGRES_DB=exemplo
POSTGRES_USER=exemplo
DATABASE_URL="postgresql://${POSTGRES_USER}:${POSTGRES_PASSWORD}@localhost:5433/${POSTGRES_DB}"
```

---

#### 6. Rodar o projeto

Agora sim, é só iniciar a aplicação:

```bash
bun run dev
```

---

Pronto. Se tudo deu certo, você já tem o backend rodando localmente e pronto pra ser testado, integrado e melhorado. Qualquer erro nesse caminho provavelmente vai estar relacionado a variáveis de ambiente ou dependências — então vale revisar isso primeiro.
