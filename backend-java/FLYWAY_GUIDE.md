# Guia Completo do Flyway

Este guia explica como usar o Flyway para gerenciar migrações de banco de dados no projeto, baseado no artigo da Baeldung.

## 📋 Índice

1. [O que é o Flyway](#o-que-é-o-flyway)
2. [Configuração Atual](#configuração-atual)
3. [Convenções de Nomenclatura](#convenções-de-nomenclatura)
4. [Comandos Principais](#comandos-principais)
5. [Criando Migrações](#criando-migrações)
6. [Melhores Práticas](#melhores-práticas)
7. [Resolução de Problemas](#resolução-de-problemas)

## 🚀 O que é o Flyway

O Flyway é uma ferramenta de migração de banco de dados que permite:
- Versionar mudanças no esquema do banco
- Aplicar migrações de forma consistente
- Rastrear o histórico de mudanças
- Facilitar deploys automatizados

## ⚙️ Configuração Atual

### Dependências no `pom.xml`
```xml
<!-- Flyway Core -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<!-- Plugin Maven -->
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <configuration>
        <url>jdbc:postgresql://localhost:5433/backend_postgres</url>
        <user>backend_user</user>
        <password>backend123</password>
        <schemas>
            <schema>public</schema>
        </schemas>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.2</version>
        </dependency>
    </dependencies>
</plugin>
```

### Configurações no `application.properties`
```properties
# Configuração do Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration
spring.flyway.table=flyway_schema_history
spring.flyway.baseline-version=0
spring.flyway.sql-migration-prefix=V
spring.flyway.sql-migration-separator=__
spring.flyway.sql-migration-suffixes=.sql
```

## 📝 Convenções de Nomenclatura

### Migrações Versionadas
```
V<VERSÃO>__<DESCRIÇÃO>.sql
```

**Exemplos:**
- `V1__create_users_table.sql` ✅
- `V2__add_roles_table.sql` ✅
- `V3__add_user_role_relationship.sql` ✅
- `V4__add_index_to_email.sql` ✅

### Migrações Repetíveis
```
R__<DESCRIÇÃO>.sql
```

**Exemplos:**
- `R__insert_default_roles.sql`
- `R__update_user_permissions.sql`

### Migrações de Desfazer (Undo)
```
U<VERSÃO>__<DESCRIÇÃO>.sql
```

**Exemplos:**
- `U1__drop_users_table.sql`
- `U2__remove_roles_table.sql`

## 🔧 Comandos Principais

### Via Maven Wrapper (Recomendado)

```bash
# Verificar status das migrações
./mvnw flyway:info

# Aplicar migrações pendentes
./mvnw flyway:migrate

# Validar migrações aplicadas
./mvnw flyway:validate

# Limpar banco de dados (CUIDADO!)
./mvnw flyway:clean

# Reparar histórico de migrações
./mvnw flyway:repair

# Criar baseline
./mvnw flyway:baseline
```

### Via Spring Boot

Quando você inicia a aplicação Spring Boot, o Flyway executa automaticamente as migrações pendentes devido à configuração `spring.flyway.enabled=true`.

## 📁 Criando Migrações

### 1. Estrutura de Diretórios
```
src/main/resources/db/migration/
├── V1__create_users_table.sql
├── V2__create_roles_table.sql
├── V3__add_user_role_relationship.sql
└── R__insert_default_data.sql
```

### 2. Exemplo de Migração Simples
```sql
-- V3__add_user_role_relationship.sql
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Índices para performance
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
```

### 3. Exemplo de Migração com Dados
```sql
-- V4__add_default_admin_user.sql
INSERT INTO users (id, name, email, password, document) 
VALUES (
    gen_random_uuid(),
    'Administrador',
    'admin@sistema.com',
    '$2a$10$encrypted_password_here',
    '00000000000'
);

-- Associar role de admin
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.email = 'admin@sistema.com' 
AND r.name = 'ADMIN';
```

## 🎯 Melhores Práticas

### 1. **Nunca Modifique Migrações Aplicadas**
- Uma vez aplicada, uma migração não deve ser alterada
- Crie uma nova migração para correções

### 2. **Use Transações**
```sql
-- Início da migração
BEGIN;

-- Suas alterações aqui
CREATE TABLE exemplo (...);

-- Confirmar mudanças
COMMIT;
```

### 3. **Teste Suas Migrações**
- Teste em ambiente local primeiro
- Use dados de teste para validar
- Verifique rollbacks quando possível

### 4. **Documente Mudanças Complexas**
```sql
-- V5__refactor_user_permissions.sql
-- 
-- Esta migração refatora o sistema de permissões:
-- 1. Remove a coluna 'permissions' da tabela users
-- 2. Cria nova tabela 'user_permissions'
-- 3. Migra dados existentes
--
-- Impacto: Melhora performance de consultas de permissões
-- Rollback: Não disponível - mudança estrutural significativa
```

### 5. **Versionamento Sequencial**
- Use números sequenciais: V1, V2, V3...
- Evite lacunas na numeração
- Coordene com a equipe para evitar conflitos

## 🔍 Resolução de Problemas

### Erro: "Checksum mismatch"
```bash
# Reparar histórico
./mvnw flyway:repair
```

### Erro: "Schema not empty"
```bash
# Criar baseline
./mvnw flyway:baseline
```

### Migração Falhou Parcialmente
```bash
# Verificar status
./mvnw flyway:info

# Reparar se necessário
./mvnw flyway:repair

# Tentar novamente
./mvnw flyway:migrate
```

### Reverter Migração (Cuidado!)
```bash
# Limpar banco (REMOVE TODOS OS DADOS!)
./mvnw flyway:clean

# Aplicar até versão específica
./mvnw flyway:migrate -Dflyway.target=2
```

## 📊 Monitoramento

### Verificar Histórico de Migrações
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

### Verificar Status via Aplicação
O Spring Boot Actuator pode expor informações do Flyway:
```properties
management.endpoints.web.exposure.include=flyway
```

## 🚨 Comandos Perigosos

⚠️ **NUNCA use em produção sem backup:**
- `flyway:clean` - Remove todos os objetos do banco
- `flyway:repair` - Modifica histórico de migrações

## 📚 Recursos Adicionais

- [Documentação Oficial do Flyway](https://flywaydb.org/documentation/)
- [Tutorial Baeldung](https://www.baeldung.com/database-migrations-with-flyway)
- [Spring Boot + Flyway](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)

---

**Lembre-se:** Sempre faça backup do banco antes de aplicar migrações em produção! 