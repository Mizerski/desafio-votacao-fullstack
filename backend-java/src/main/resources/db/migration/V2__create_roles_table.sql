-- Migration para criar tabela de roles (funções/papéis)
-- Esta migration foi criada para manter consistência com o histórico do Flyway

CREATE TABLE roles (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Inserir roles padrão
INSERT INTO roles (id, name, description) VALUES 
    (gen_random_uuid()::text, 'ADMIN', 'Administrador do sistema'),
    (gen_random_uuid()::text, 'USER', 'Usuário comum'),
    (gen_random_uuid()::text, 'MODERATOR', 'Moderador de votações');

-- Índices
CREATE INDEX idx_roles_name ON roles(name);

-- Comentários
COMMENT ON TABLE roles IS 'Tabela de papéis/funções dos usuários';
COMMENT ON COLUMN roles.name IS 'Nome único do papel/função';
COMMENT ON COLUMN roles.description IS 'Descrição do papel/função'; 