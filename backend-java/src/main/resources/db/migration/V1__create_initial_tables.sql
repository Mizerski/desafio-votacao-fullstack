-- Migration inicial para criar as tabelas do sistema de votação
-- Baseada nas entidades JPA atuais do projeto

-- Tabela de usuários
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    document VARCHAR(255) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de agendas
CREATE TABLE agendas (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (
        status IN (
            'DRAFT',
            'OPEN',
            'IN_PROGRESS',
            'FINISHED',
            'CANCELLED',
            'ALL'
        )
    ),
    category VARCHAR(50) NOT NULL CHECK (
        category IN (
            'PROJETOS',
            'ADMINISTRATIVO',
            'ELEICOES',
            'ESTATUTARIO',
            'FINANCEIRO',
            'OUTROS',
            'ALL'
        )
    ),
    result VARCHAR(50) NOT NULL CHECK (
        result IN (
            'APPROVED',
            'REJECTED',
            'TIE',
            'UNVOTED',
            'ALL'
        )
    ),
    total_votes INTEGER NOT NULL DEFAULT 0,
    yes_votes INTEGER NOT NULL DEFAULT 0,
    no_votes INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de sessões de votação
CREATE TABLE sessions (
    id VARCHAR(36) PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    agenda_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sessions_agenda FOREIGN KEY (agenda_id) REFERENCES agendas (id) ON DELETE CASCADE
);

-- Tabela de votos
CREATE TABLE votes (
    id VARCHAR(36) PRIMARY KEY,
    vote_type VARCHAR(10) NOT NULL CHECK (vote_type IN ('YES', 'NO')),
    user_id VARCHAR(36) NOT NULL,
    agenda_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_votes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_votes_agenda FOREIGN KEY (agenda_id) REFERENCES agendas (id) ON DELETE CASCADE,
    CONSTRAINT uk_user_agenda_vote UNIQUE (user_id, agenda_id)
);

-- Índices para melhorar performance das consultas
CREATE INDEX idx_users_email ON users (email);

CREATE INDEX idx_users_document ON users (document);

CREATE INDEX idx_users_created_at ON users (created_at);

CREATE INDEX idx_agendas_status ON agendas (status);

CREATE INDEX idx_agendas_category ON agendas (category);

CREATE INDEX idx_agendas_result ON agendas (result);

CREATE INDEX idx_agendas_is_active ON agendas (is_active);

CREATE INDEX idx_agendas_title ON agendas (title);

CREATE INDEX idx_agendas_created_at ON agendas (created_at);

CREATE INDEX idx_sessions_agenda_id ON sessions (agenda_id);

CREATE INDEX idx_sessions_start_time ON sessions (start_time);

CREATE INDEX idx_sessions_end_time ON sessions (end_time);

CREATE INDEX idx_sessions_created_at ON sessions (created_at);

CREATE INDEX idx_votes_user_id ON votes (user_id);

CREATE INDEX idx_votes_agenda_id ON votes (agenda_id);

CREATE INDEX idx_votes_vote_type ON votes (vote_type);

CREATE INDEX idx_votes_created_at ON votes (created_at);

-- Comentários nas tabelas para documentação
COMMENT ON TABLE users IS 'Tabela de usuários do sistema de votação';

COMMENT ON TABLE agendas IS 'Tabela de agendas/pautas de votação';

COMMENT ON TABLE sessions IS 'Tabela de sessões de votação com horários definidos';

COMMENT ON TABLE votes IS 'Tabela de votos dos usuários nas agendas';

-- Comentários nas colunas principais para melhor documentação
COMMENT ON COLUMN users.id IS 'Identificador único do usuário (UUID)';

COMMENT ON COLUMN users.name IS 'Nome completo do usuário';

COMMENT ON COLUMN users.email IS 'Email único do usuário para login';

COMMENT ON COLUMN users.password IS 'Senha criptografada do usuário';

COMMENT ON COLUMN users.document IS 'Documento de identificação (CPF, RG, etc.)';

COMMENT ON COLUMN agendas.id IS 'Identificador único da agenda (UUID)';

COMMENT ON COLUMN agendas.title IS 'Título da agenda de votação';

COMMENT ON COLUMN agendas.description IS 'Descrição detalhada da agenda';

COMMENT ON COLUMN agendas.status IS 'Status atual da agenda (DRAFT, OPEN, IN_PROGRESS, FINISHED, CANCELLED)';

COMMENT ON COLUMN agendas.category IS 'Categoria da agenda (PROJETOS, ADMINISTRATIVO, ELEICOES, etc.)';

COMMENT ON COLUMN agendas.result IS 'Resultado da votação (APPROVED, REJECTED, TIE, UNVOTED)';

COMMENT ON COLUMN agendas.total_votes IS 'Total de votos computados para esta agenda';

COMMENT ON COLUMN agendas.yes_votes IS 'Quantidade de votos SIM';

COMMENT ON COLUMN agendas.no_votes IS 'Quantidade de votos NÃO';

COMMENT ON COLUMN agendas.is_active IS 'Indica se a agenda está ativa no sistema';

COMMENT ON COLUMN sessions.id IS 'Identificador único da sessão (UUID)';

COMMENT ON COLUMN sessions.start_time IS 'Data e hora de início da sessão de votação';

COMMENT ON COLUMN sessions.end_time IS 'Data e hora de fim da sessão de votação';

COMMENT ON COLUMN sessions.agenda_id IS 'Referência para a agenda desta sessão';

COMMENT ON COLUMN votes.id IS 'Identificador único do voto (UUID)';

COMMENT ON COLUMN votes.vote_type IS 'Tipo do voto: YES (sim) ou NO (não)';

COMMENT ON COLUMN votes.user_id IS 'Referência para o usuário que votou';

COMMENT ON COLUMN votes.agenda_id IS 'Referência para a agenda votada';