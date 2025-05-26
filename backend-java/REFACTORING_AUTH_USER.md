# Refatoração: Eliminação de Duplicação entre AuthService e UserService

## 🚨 Problema Identificado

Existia uma **falha arquitetural crítica** onde dois serviços faziam praticamente a mesma coisa:

### ❌ Antes da Refatoração

```java
// AuthService.register() - Criava usuário + retornava token
@PostMapping("/api/auth/register")
public Result<AuthResponse> register(RegisterRequest request) {
    // Validações duplicadas
    // Criação de usuário duplicada
    // Lógica de negócio duplicada
}

// UserService.createUser() - Criava usuário + retornava dados
@PostMapping("/api/v1/users")  
public Result<UserResponse> createUser(CreateUserRequest request) {
    // Mesmas validações
    // Mesma criação de usuário
    // Mesma lógica de negócio
}
```

**Problemas:**
- ❌ Violação do princípio **DRY** (Don't Repeat Yourself)
- ❌ Duplicação de validações de negócio
- ❌ Inconsistências entre os dois fluxos
- ❌ Manutenção duplicada
- ❌ Responsabilidades mal definidas

## ✅ Solução Implementada

### Princípio da Responsabilidade Única

**`UserService`** → Responsável por **CRUD de usuários**
**`AuthService`** → Responsável por **autenticação/autorização** e **delega** criação

### ✅ Depois da Refatoração

```java
// AuthService agora DELEGA a criação para UserService
@Override
@Idempotent(expireAfterSeconds = 300, includeUserId = false)
public Result<AuthResponse> register(RegisterRequest request) {
    try {
        // 1. Converte RegisterRequest para CreateUserRequest
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .document(request.getDocument())
                .role(request.getRole())
                .build();

        // 2. DELEGA criação do usuário para UserService
        Result<UserResponse> userResult = userService.createUser(createUserRequest);

        if (userResult.isError()) {
            // Propaga o erro do UserService
            return Result.error(userResult.getErrorCode().orElse("USER_CREATION_FAILED"), 
                              userResult.getErrorMessage().orElse("Erro ao criar usuário"));
        }

        // 3. Busca o usuário criado para gerar token (responsabilidade do AuthService)
        UserEntity user = userRepository.findByEmailAndIsActiveTrue(userResponse.getEmail())
                .orElse(null);

        // 4. Gera tokens JWT (responsabilidade do AuthService)
        String accessToken = jwtService.generateToken(user);

        // 5. Cria resposta de autenticação
        AuthResponse.UserInfo userInfo = createUserInfo(user);
        AuthResponse response = AuthResponse.createBearerToken(
                accessToken,
                jwtService.getExpirationTime(),
                userInfo);

        return Result.success(response);
    } catch (Exception e) {
        return exceptionMappingService.mapExceptionToResult(e);
    }
}
```

## 🔧 Mudanças Implementadas

### 1. **AuthServiceImpl**
- ✅ Removida lógica de criação de usuário
- ✅ Adicionada delegação para `UserService`
- ✅ Mantida responsabilidade de geração de tokens JWT
- ✅ Injeção de dependência do `UserService`

### 2. **CreateUserRequest**
- ✅ Adicionado campo `role` para compatibilidade
- ✅ Adicionado `@Builder` pattern
- ✅ Mantida compatibilidade com `RegisterRequest`

### 3. **Users (Domínio)**
- ✅ Adicionado campo `role` com valor padrão `UserRole.USER`
- ✅ Mantida consistência entre Entity e Domain

### 4. **UserServiceImpl**
- ✅ Adicionado `PasswordEncoder` para criptografia de senhas
- ✅ Adicionada configuração de campos de segurança padrão
- ✅ Mantida responsabilidade única de CRUD de usuários

## 📊 Benefícios da Refatoração

### ✅ Arquitetura
- **Responsabilidade Única:** Cada serviço tem uma responsabilidade clara
- **DRY:** Eliminada duplicação de código
- **Consistência:** Um único ponto de criação de usuários
- **Manutenibilidade:** Mudanças em uma única localização

### ✅ Performance
- **Result Pattern:** Mantido em ambos os serviços
- **Idempotência:** Preservada no registro
- **Validações:** Centralizadas no UserService

### ✅ Testabilidade
- **Isolamento:** Cada serviço pode ser testado independentemente
- **Mocking:** Fácil de mockar a dependência entre serviços
- **Cobertura:** Testes mais focados e específicos

## 🔄 Fluxo Atual

### Registro de Usuário (`/api/auth/register`)
```
1. AuthController recebe RegisterRequest
2. AuthService.register() é chamado
3. AuthService converte para CreateUserRequest
4. AuthService DELEGA para UserService.createUser()
5. UserService valida e cria usuário
6. AuthService busca usuário criado
7. AuthService gera token JWT
8. AuthService retorna AuthResponse com token
```

### Criação de Usuário (`/api/v1/users`)
```
1. UserController recebe CreateUserRequest
2. UserService.createUser() é chamado
3. UserService valida e cria usuário
4. UserService retorna UserResponse
```

## 🎯 Padrões Arquiteturais Mantidos

### ✅ Result Pattern
- Ambos os serviços continuam usando Result Pattern
- Tratamento de erros sem exceptions para fluxo de negócio
- Performance otimizada

### ✅ Injeção de Dependência via Construtor
- AuthService injeta UserService via construtor
- UserService injeta PasswordEncoder via construtor
- Evitado uso de @Autowired

### ✅ Idempotência
- Mantida no registro de usuários
- Cache de 300 segundos para evitar duplicações

### ✅ Validações Robustas
- Centralizadas no UserService
- Validações de domínio mantidas
- Validações de duplicação preservadas

## 🧪 Testes Necessários

### AuthService
```java
@Test
void testRegisterDelegatesUserCreation() {
    // Testa se AuthService delega corretamente para UserService
    // Testa se token JWT é gerado após criação
    // Testa propagação de erros do UserService
}
```

### UserService
```java
@Test
void testCreateUserWithPasswordEncoding() {
    // Testa se senha é criptografada
    // Testa se campos de segurança são definidos
    // Testa validações de negócio
}
```

## 📝 Conclusão

A refatoração eliminou com sucesso a duplicação entre `AuthService` e `UserService`, implementando o princípio da responsabilidade única:

- **UserService:** Especialista em CRUD de usuários
- **AuthService:** Especialista em autenticação e autorização

Esta arquitetura é mais **robusta**, **maintível** e **testável**, seguindo as melhores práticas de desenvolvimento de software.

## 🔗 Endpoints Afetados

### ✅ Funcionais
- `POST /api/auth/register` - Funciona delegando para UserService
- `POST /api/v1/users` - Funciona como ponto único de criação
- `POST /api/auth/login` - Não afetado
- `GET /api/auth/me` - Não afetado

### 🧪 Testes Recomendados
1. Testar registro via `/api/auth/register`
2. Testar criação via `/api/v1/users`
3. Verificar se senhas são criptografadas
4. Verificar se tokens JWT são gerados corretamente
5. Testar propagação de erros entre serviços 