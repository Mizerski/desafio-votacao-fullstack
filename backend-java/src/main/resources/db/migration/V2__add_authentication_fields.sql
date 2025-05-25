-- Migração para adicionar campos de autenticação na tabela users
-- V2__add_authentication_fields.sql

-- Adiciona campos de role e controle de conta
ALTER TABLE users
ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'USER',
ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT true,
ADD COLUMN IF NOT EXISTS is_email_verified BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN IF NOT EXISTS is_account_non_expired BOOLEAN NOT NULL DEFAULT true,
ADD COLUMN IF NOT EXISTS is_account_non_locked BOOLEAN NOT NULL DEFAULT true,
ADD COLUMN IF NOT EXISTS is_credentials_non_expired BOOLEAN NOT NULL DEFAULT true,
ADD COLUMN IF NOT EXISTS last_login TIMESTAMP;

-- Adiciona comentários para documentação
COMMENT ON COLUMN users.role IS 'Role do usuário no sistema (USER, ADMIN, MODERATOR)';

COMMENT ON COLUMN users.is_active IS 'Indica se a conta do usuário está ativa';

COMMENT ON COLUMN users.is_email_verified IS 'Indica se o email foi verificado';

COMMENT ON COLUMN users.is_account_non_expired IS 'Indica se a conta não está expirada';

COMMENT ON COLUMN users.is_account_non_locked IS 'Indica se a conta não está bloqueada';

COMMENT ON COLUMN users.is_credentials_non_expired IS 'Indica se as credenciais não estão expiradas';

COMMENT ON COLUMN users.last_login IS 'Timestamp do último login do usuário';

-- Cria índices para melhorar performance
CREATE INDEX IF NOT EXISTS idx_users_email_active ON users (email, is_active);

CREATE INDEX IF NOT EXISTS idx_users_role ON users (role);

CREATE INDEX IF NOT EXISTS idx_users_last_login ON users (last_login);

-- Atualiza usuários existentes para ter role USER por padrão
UPDATE users SET role = 'USER' WHERE role IS NULL;

-- Adiciona constraint para validar roles
ALTER TABLE users
ADD CONSTRAINT chk_user_role CHECK (
    role IN ('USER', 'ADMIN', 'MODERATOR')
);