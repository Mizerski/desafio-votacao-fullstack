# Sistema de Autenticação JWT

Este documento descreve o sistema completo de autenticação implementado no projeto, seguindo os padrões arquiteturais estabelecidos.

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Endpoints da API](#endpoints-da-api)
- [Configuração](#configuração)
- [Exemplos de Uso](#exemplos-de-uso)
- [Segurança](#segurança)
- [Testes](#testes)

## 🎯 Visão Geral

O sistema de autenticação implementa:

- **JWT (JSON Web Tokens)** para autenticação stateless
- **Result Pattern** para tratamento de erros sem exceptions
- **Spring Security** para controle de acesso
- **Roles e permissões** (USER, ADMIN, MODERATOR)
- **Refresh tokens** para renovação automática
- **Idempotência** em operações críticas
- **Validações robustas** com anotações customizadas

## 🏗️ Arquitetura

### Componentes Principais

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   AuthController │────│   AuthService   │────│  UserRepository │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │              ┌─────────────────┐              │
         └──────────────│   JwtService    │──────────────┘
                        └─────────────────┘
                                 │
                        ┌─────────────────┐
                        │ SecurityConfig  │
                        └─────────────────┘
```

### Fluxo de Autenticação

1. **Login**: Usuário envia credenciais
2. **Validação**: Sistema valida email/senha
3. **Token Generation**: Gera JWT access + refresh token
4. **Response**: Retorna tokens e dados do usuário
5. **Requests**: Cliente inclui token no header Authorization
6. **Validation**: Filtro JWT valida token em cada requisição

## 🔌 Endpoints da API

### Autenticação

#### POST `/api/auth/login`
Realiza login do usuário.

**Request:**
```json
{
  "email": "usuario@example.com",
  "password": "senha123"
}
```

**Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": "uuid",
    "name": "Nome do Usuário",
    "email": "usuario@example.com",
    "role": "USER",
    "isActive": true,
    "isEmailVerified": false,
    "lastLogin": "2024-01-15T10:30:00"
  }
}
```

#### POST `/api/auth/register`
Registra novo usuário.

**Request:**
```json
{
  "name": "Novo Usuário",
  "email": "novo@example.com",
  "password": "senha123",
  "document": "12345678901",
  "role": "USER"
}
```

**Response (201):** Mesmo formato do login

#### POST `/api/auth/refresh`
Atualiza token usando refresh token.

**Request:**
```
POST /api/auth/refresh?refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200):** Novo token JWT

#### POST `/api/auth/logout`
Realiza logout (invalida token).

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200):** Status de sucesso

#### GET `/api/auth/validate`
Valida se token é válido.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200):**
```json
true
```

#### GET `/api/auth/me`
Obtém dados do usuário autenticado.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200):** Dados do usuário

## ⚙️ Configuração

### Variáveis de Ambiente

```bash
# JWT Configuration
JWT_SECRET=sua_chave_secreta_aqui
JWT_EXPIRATION=86400000          # 24 horas
JWT_REFRESH_EXPIRATION=604800000 # 7 dias

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
```

### application.yml

```yaml
jwt:
  secret: ${JWT_SECRET:chave_padrao}
  expiration: ${JWT_EXPIRATION:86400000}
  refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800000}

security:
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:3000}
    allowed-methods: GET,POST,PUT,DELETE,PATCH,OPTIONS
    allowed-headers: "*"
    allow-credentials: true
```

## 💡 Exemplos de Uso

### Frontend (JavaScript)

```javascript
// Login
const login = async (email, password) => {
  const response = await fetch('/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ email, password }),
  });
  
  const data = await response.json();
  
  if (response.ok) {
    // Armazena token
    localStorage.setItem('token', data.token);
    localStorage.setItem('refreshToken', data.refreshToken);
    return data;
  }
  
  throw new Error(data.message);
};

// Requisição autenticada
const fetchProtectedData = async () => {
  const token = localStorage.getItem('token');
  
  const response = await fetch('/api/protected-endpoint', {
    headers: {
      'Authorization': `Bearer ${token}`,
    },
  });
  
  if (response.status === 401) {
    // Token expirado, tenta refresh
    await refreshToken();
    return fetchProtectedData(); // Retry
  }
  
  return response.json();
};

// Refresh token
const refreshToken = async () => {
  const refreshToken = localStorage.getItem('refreshToken');
  
  const response = await fetch(`/api/auth/refresh?refreshToken=${refreshToken}`, {
    method: 'POST',
  });
  
  if (response.ok) {
    const data = await response.json();
    localStorage.setItem('token', data.token);
    return data;
  }
  
  // Refresh falhou, redireciona para login
  localStorage.clear();
  window.location.href = '/login';
};
```

### cURL Examples

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }'

# Requisição autenticada
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Registro
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Novo Usuário",
    "email": "novo@example.com",
    "password": "senha123"
  }'
```

## 🔒 Segurança

### Recursos Implementados

1. **Criptografia de Senhas**: BCrypt com salt
2. **JWT Seguro**: Assinatura HMAC SHA-256
3. **Validação de Entrada**: Bean Validation
4. **CORS Configurado**: Origens permitidas específicas
5. **Rate Limiting**: Via idempotência
6. **Logs de Segurança**: Tentativas de login

### Boas Práticas

- ✅ Senhas nunca retornadas nas APIs
- ✅ Tokens com tempo de expiração
- ✅ Refresh tokens para renovação
- ✅ Validação de roles em endpoints
- ✅ Headers de segurança configurados
- ✅ Logs de auditoria

### Configuração de Roles

```java
// Endpoint apenas para ADMIN
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/users")
public ResponseEntity<List<User>> getAllUsers() {
    // ...
}

// Endpoint para USER ou ADMIN
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@GetMapping("/profile")
public ResponseEntity<User> getProfile() {
    // ...
}
```

## 🧪 Testes

### Executar Testes

```bash
# Todos os testes
mvn test

# Apenas testes de autenticação
mvn test -Dtest=AuthServiceTest

# Com coverage
mvn test jacoco:report
```

### Cenários Testados

- ✅ Login com credenciais válidas
- ✅ Login com credenciais inválidas
- ✅ Registro de novo usuário
- ✅ Registro com email duplicado
- ✅ Validação de token válido
- ✅ Validação de token inválido
- ✅ Refresh token válido
- ✅ Refresh token expirado

## 🚀 Deployment

### Configuração de Produção

```yaml
# application-prod.yml
jwt:
  secret: ${JWT_SECRET} # Obrigatório em produção
  expiration: 3600000   # 1 hora em produção
  
logging:
  level:
    org.springframework.security: WARN
    com.mizerski.backend: INFO
```

### Variáveis de Ambiente Obrigatórias

```bash
JWT_SECRET=sua_chave_super_secreta_de_pelo_menos_256_bits
DATABASE_URL=jdbc:postgresql://prod-db:5432/backend
CORS_ALLOWED_ORIGINS=https://seu-frontend.com
```

## 📊 Monitoramento

### Métricas Disponíveis

- `/actuator/health` - Status da aplicação
- `/actuator/metrics` - Métricas gerais
- Logs estruturados para auditoria

### Logs de Auditoria

```
2024-01-15 10:30:00 - Login realizado com sucesso para usuário: admin@example.com
2024-01-15 10:31:00 - Falha na autenticação para email: hacker@evil.com
2024-01-15 10:32:00 - Token atualizado com sucesso para usuário: user@example.com
```

## 🔧 Troubleshooting

### Problemas Comuns

1. **Token inválido**: Verificar se o secret está correto
2. **CORS errors**: Configurar allowed-origins
3. **401 Unauthorized**: Verificar formato do header Authorization
4. **403 Forbidden**: Verificar roles do usuário

### Debug

```yaml
logging:
  level:
    org.springframework.security: DEBUG
    com.mizerski.backend.config: DEBUG
```

---

## 📝 Conclusão

O sistema de autenticação implementado segue as melhores práticas de segurança e arquitetura, utilizando:

- **Result Pattern** para tratamento robusto de erros
- **JWT** para autenticação stateless e escalável
- **Spring Security** para controle de acesso granular
- **Idempotência** para operações críticas
- **Validações** abrangentes em todas as camadas

O sistema está pronto para produção e pode ser facilmente estendido com funcionalidades adicionais como verificação de email, autenticação de dois fatores, ou integração com provedores OAuth. 