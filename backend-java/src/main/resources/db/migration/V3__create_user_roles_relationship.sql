-- Migration para criar relacionamento entre usuários e roles
-- Esta migration foi criada para manter consistência com o histórico do Flyway

CREATE TABLE user_roles (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
);

-- Índices para melhorar performance
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- Comentários
COMMENT ON TABLE user_roles IS 'Tabela de relacionamento entre usuários e seus papéis/funções';
COMMENT ON COLUMN user_roles.user_id IS 'ID do usuário';
COMMENT ON COLUMN user_roles.role_id IS 'ID do papel/função'; 