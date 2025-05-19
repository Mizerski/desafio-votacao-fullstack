# Desafio Votação Fullstack

Sistema completo de votação digital com backend robusto e frontend moderno. Ideal para assembleias, conselhos ou qualquer processo de votação estruturado.

## O que temos por aqui?

Este projeto é dividido em duas partes principais:

### [Backend](/backend)

API completa construída com:
- **Fastify** para rotas rápidas e eficientes
- **Prisma** como ORM para PostgreSQL
- **JWT** para autenticação segura
- **Zod** para validação de dados
- **TypeScript** para tipagem forte

[Ver detalhes completos do Backend →](/backend/README.md)

### [Frontend](/web)

Interface web moderna desenvolvida com:
- **React 19** com hooks customizados
- **TypeScript** para desenvolvimento seguro
- **React Hook Form + Zod** para validação de formulários
- **TailwindCSS** para UI responsiva
- **React Router** para navegação fluida

[Ver detalhes completos do Frontend →](/web/README.md)

## Arquitetura geral

O sistema segue uma arquitetura cliente-servidor com comunicação via API REST:

```
┌─────────────┐      HTTP/REST      ┌─────────────┐
│             │ <---------------->  │             │
│  Frontend   │                     │  Backend    │
│  (React)    │ ------------------> │  (Fastify)  │
│             │    JSON/JWT Auth    │             │
└─────────────┘                     └─────────────┘
                                          │
                                          │ Prisma ORM
                                          ▼
                                    ┌─────────────┐
                                    │             │
                                    │ PostgreSQL  │
                                    │             │
                                    └─────────────┘
```

## Fluxos principais

1. **Autenticação:** Cadastro e login de usuários com JWT
2. **Gestão de pautas:** Criação e visualização de pautas para votação
3. **Processo de votação:** Abertura de sessão, votação (SIM/NÃO) e contabilização
4. **Resultados:** Visualização de resultados em tempo real e histórico

## Começando

Cada projeto (backend e frontend) tem seu próprio guia de instalação e execução.

Para rodar o projeto completo:

1. Primeiro, configure e inicie o [backend](/backend/README.md)
2. Em seguida, configure e inicie o [frontend](/web/README.md)

## Tecnologias

O stack completo usa tecnologias modernas e bem estabelecidas:

- Node.js v18+
- PostgreSQL (via Docker)
- React 19
- TypeScript
- TailwindCSS
- Vários utilitários de qualidade de código (ESLint, etc)

## Contribuindo

Sinta-se à vontade para abrir issues, propor melhorias ou enviar pull requests. Todo código novo deve seguir os padrões de estilo estabelecidos e incluir testes quando apropriado.

---

Desenvolvido com 💙 por mizerski
