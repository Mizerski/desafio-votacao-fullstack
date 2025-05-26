# Modelo de Dados

## Diagrama Entidade-Relacionamento

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     USERS       │       │     VOTES       │       │    AGENDAS      │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ id (PK)         │◄──────┤ user_id (FK)    │       │ id (PK)         │
│ name            │       │ agenda_id (FK)  ├──────►│ title           │
│ email (UNIQUE)  │       │ vote_type       │       │ description     │
│ password        │       │ created_at      │       │ status          │
│ document        │       │ updated_at      │       │ category        │
│ created_at      │       │                 │       │ result          │
│ updated_at      │       │ CONSTRAINT:     │       │ total_votes     │
└─────────────────┘       │ uk_user_agenda  │       │ yes_votes       │
                          └─────────────────┘       │ no_votes        │
                                                    │ is_active       │
                                                    │ created_at      │
                                                    │ updated_at      │
                                                    └─────────────────┘
                                                             │
                                                             │
                                                             ▼
                                                    ┌─────────────────┐
                                                    │    SESSIONS     │
                                                    ├─────────────────┤
                                                    │ id (PK)         │
                                                    │ agenda_id (FK)  │
                                                    │ start_time      │
                                                    │ end_time        │
                                                    │ created_at      │
                                                    │ updated_at      │
                                                    └─────────────────┘
```

## Especificações das Tabelas

### Tabela USERS
```sql
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,                    -- UUID gerado automaticamente
    name VARCHAR(255) NOT NULL,                    -- Nome completo do usuário
    email VARCHAR(255) NOT NULL UNIQUE,            -- Email único para login
    password VARCHAR(255) NOT NULL,                -- Senha criptografada
    document VARCHAR(255) UNIQUE,                  -- CPF/RG (opcional)
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),   -- Data de criação
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()    -- Data de atualização
);
```

### Tabela AGENDAS
```sql
CREATE TABLE agendas (
    id VARCHAR(36) PRIMARY KEY,                    -- UUID gerado automaticamente
    title VARCHAR(255) NOT NULL,                   -- Título da agenda
    description TEXT NOT NULL,                     -- Descrição detalhada
    status VARCHAR(50) NOT NULL,                   -- DRAFT, OPEN, IN_PROGRESS, FINISHED, CANCELLED
    category VARCHAR(50) NOT NULL,                 -- PROJETOS, ADMINISTRATIVO, ELEICOES, etc.
    result VARCHAR(50) NOT NULL,                   -- APPROVED, REJECTED, TIE, UNVOTED
    total_votes INTEGER NOT NULL DEFAULT 0,        -- Total de votos computados
    yes_votes INTEGER NOT NULL DEFAULT 0,          -- Quantidade de votos SIM
    no_votes INTEGER NOT NULL DEFAULT 0,           -- Quantidade de votos NÃO
    is_active BOOLEAN NOT NULL DEFAULT true,       -- Agenda ativa no sistema
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),   -- Data de criação
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()    -- Data de atualização
);
```

### Tabela VOTES
```sql
CREATE TABLE votes (
    id VARCHAR(36) PRIMARY KEY,                    -- UUID gerado automaticamente
    vote_type VARCHAR(10) NOT NULL,                -- YES ou NO
    user_id VARCHAR(36) NOT NULL,                  -- Referência para users.id
    agenda_id VARCHAR(36) NOT NULL,                -- Referência para agendas.id
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),   -- Data de criação
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),   -- Data de atualização
    
    -- Constraints
    CONSTRAINT fk_votes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_votes_agenda FOREIGN KEY (agenda_id) REFERENCES agendas(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_agenda_vote UNIQUE (user_id, agenda_id),  -- Um voto por usuário por agenda
    CONSTRAINT chk_vote_type CHECK (vote_type IN ('YES', 'NO'))
);
```

### Tabela SESSIONS
```sql
CREATE TABLE sessions (
    id VARCHAR(36) PRIMARY KEY,                    -- UUID gerado automaticamente
    start_time TIMESTAMP NOT NULL,                 -- Início da sessão de votação
    end_time TIMESTAMP NOT NULL,                   -- Fim da sessão de votação
    agenda_id VARCHAR(36) NOT NULL,                -- Referência para agendas.id
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),   -- Data de criação
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),   -- Data de atualização
    
    -- Constraints
    CONSTRAINT fk_sessions_agenda FOREIGN KEY (agenda_id) REFERENCES agendas(id) ON DELETE CASCADE
);
```

## Índices Implementados
```sql
-- Performance otimizada para consultas frequentes
CREATE INDEX idx_users_email ON users(email);              -- Login por email
CREATE INDEX idx_agendas_status ON agendas(status);        -- Filtro por status
CREATE INDEX idx_agendas_category ON agendas(category);    -- Filtro por categoria
CREATE INDEX idx_votes_user_id ON votes(user_id);          -- Votos por usuário
CREATE INDEX idx_votes_agenda_id ON votes(agenda_id);      -- Votos por agenda
CREATE INDEX idx_sessions_agenda_id ON sessions(agenda_id); -- Sessões por agenda
```

## Enums do Sistema

O sistema utiliza enums Java que são mapeados como VARCHAR no banco com constraints CHECK:

```java
// Status das agendas
public enum AgendaStatus {
    DRAFT,          // Rascunho (não publicada)
    OPEN,           // Aberta para votação
    IN_PROGRESS,    // Votação em andamento
    FINISHED,       // Votação finalizada
    CANCELLED,      // Cancelada
    ALL             // Filtro para buscar todas
}

// Categorias das agendas
public enum AgendaCategory {
    PROJETOS,       // Projetos e iniciativas
    ADMINISTRATIVO, // Questões administrativas
    ELEICOES,       // Processos eleitorais
    ESTATUTARIO,    // Mudanças no estatuto
    FINANCEIRO,     // Questões financeiras
    OUTROS,         // Outras categorias
    ALL             // Filtro para buscar todas
}

// Resultado da votação
public enum AgendaResult {
    APPROVED,       // Aprovada (mais votos SIM)
    REJECTED,       // Rejeitada (mais votos NÃO)
    TIE,           // Empate
    UNVOTED,       // Ainda não votada
    ALL            // Filtro para buscar todas
}

// Tipo do voto
public enum VoteType {
    YES,           // Voto favorável
    NO             // Voto contrário
}
``` 