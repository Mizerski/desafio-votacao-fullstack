-- Migration inicial para criar as tabelas do sistema de votação
-- Criada baseada nas entidades JPA do projeto

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
    status VARCHAR(50) NOT NULL CHECK (status IN ('OPEN', 'IN_PROGRESS', 'FINISHED', 'CANCELLED', 'ALL')),
    category VARCHAR(50) NOT NULL CHECK (category IN ('PROJETOS', 'ADMINISTRATIVO', 'ELEICOES', 'ESTATUTARIO', 'FINANCEIRO', 'OUTROS', 'ALL')),
    result VARCHAR(50) NOT NULL CHECK (result IN ('APPROVED', 'REJECTED', 'TIE', 'UNVOTED', 'ALL')),
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
    CONSTRAINT fk_sessions_agenda FOREIGN KEY (agenda_id) REFERENCES agendas(id) ON DELETE CASCADE
);

-- Tabela de votos
CREATE TABLE votes (
    id VARCHAR(36) PRIMARY KEY,
    vote_type VARCHAR(10) NOT NULL CHECK (vote_type IN ('YES', 'NO')),
    user_id VARCHAR(36) NOT NULL,
    agenda_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_votes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_votes_agenda FOREIGN KEY (agenda_id) REFERENCES agendas(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_agenda_vote UNIQUE (user_id, agenda_id)
);

-- Índices para melhorar performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_document ON users(document);
CREATE INDEX idx_agendas_status ON agendas(status);
CREATE INDEX idx_agendas_category ON agendas(category);
CREATE INDEX idx_agendas_is_active ON agendas(is_active);
CREATE INDEX idx_sessions_agenda_id ON sessions(agenda_id);
CREATE INDEX idx_sessions_start_time ON sessions(start_time);
CREATE INDEX idx_votes_user_id ON votes(user_id);
CREATE INDEX idx_votes_agenda_id ON votes(agenda_id);
CREATE INDEX idx_votes_vote_type ON votes(vote_type);

-- Comentários nas tabelas
COMMENT ON TABLE users IS 'Tabela de usuários do sistema';
COMMENT ON TABLE agendas IS 'Tabela de agendas de votação';
COMMENT ON TABLE sessions IS 'Tabela de sessões de votação';
COMMENT ON TABLE votes IS 'Tabela de votos dos usuários';

-- Comentários nas colunas principais
COMMENT ON COLUMN users.document IS 'Documento de identificação do usuário (CPF, RG, etc.)';
COMMENT ON COLUMN agendas.total_votes IS 'Total de votos computados para esta agenda';
COMMENT ON COLUMN agendas.yes_votes IS 'Quantidade de votos SIM';
COMMENT ON COLUMN agendas.no_votes IS 'Quantidade de votos NÃO';
COMMENT ON COLUMN sessions.start_time IS 'Data e hora de início da sessão de votação';
COMMENT ON COLUMN sessions.end_time IS 'Data e hora de fim da sessão de votação';
COMMENT ON COLUMN votes.vote_type IS 'Tipo do voto: YES ou NO'; 