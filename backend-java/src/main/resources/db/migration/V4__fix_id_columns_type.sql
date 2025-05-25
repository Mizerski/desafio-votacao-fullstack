-- Migration para corrigir o tipo das colunas ID
-- Alterando de UUID para VARCHAR para compatibilidade com as entidades JPA

-- Primeiro, vamos remover as constraints de foreign key temporariamente
ALTER TABLE votes DROP CONSTRAINT IF EXISTS votes_user_id_fkey;
ALTER TABLE votes DROP CONSTRAINT IF EXISTS votes_agenda_id_fkey;

-- Alterar o tipo das colunas ID para VARCHAR apenas nas tabelas que existem
ALTER TABLE users ALTER COLUMN id TYPE VARCHAR(36);
ALTER TABLE agendas ALTER COLUMN id TYPE VARCHAR(36);
ALTER TABLE votes ALTER COLUMN id TYPE VARCHAR(36);

-- Alterar as colunas de foreign key também
ALTER TABLE votes ALTER COLUMN user_id TYPE VARCHAR(36);
ALTER TABLE votes ALTER COLUMN agenda_id TYPE VARCHAR(36);

-- Recriar as constraints de foreign key
ALTER TABLE votes ADD CONSTRAINT votes_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE votes ADD CONSTRAINT votes_agenda_id_fkey FOREIGN KEY (agenda_id) REFERENCES agendas(id) ON DELETE CASCADE;

-- Comentário
COMMENT ON COLUMN users.id IS 'Identificador único do usuário (VARCHAR para compatibilidade JPA)';
COMMENT ON COLUMN agendas.id IS 'Identificador único da agenda (VARCHAR para compatibilidade JPA)';
COMMENT ON COLUMN votes.id IS 'Identificador único do voto (VARCHAR para compatibilidade JPA)'; 