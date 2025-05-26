# Testes e Qualidade

## Estrutura de Testes Implementada

```
src/test/java/com/mizerski/backend/
└── services/                           # Testes unitários dos serviços
    ├── AgendaServiceTest.java          # 15 testes - CRUD e regras de negócio
    ├── AgendaTimeServiceTest.java      # 12 testes - Timers e cálculos
    ├── VoteServiceTest.java            # 8 testes - Votação e validações
    ├── VoteServiceIntegrationTest.java # 6 testes - Integração com mapeamento de erros
    ├── ExceptionMappingServiceTest.java # 16 testes - Mapeamento de exceções
    ├── ErrorMappingServiceTest.java    # 12 testes - Mapeamento de erros HTTP
    └── IdempotencyServiceTest.java     # 20 testes - Cache e thread safety
```

**Total: 89 testes unitários com 100% de sucesso**

## Cobertura de Testes por Serviço

### 1. **AgendaServiceTest** (15 testes)
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("AgendaService - Testes Unitários")
class AgendaServiceTest {
    
    @Nested
    @DisplayName("Testes de criação de agenda")
    class CreateAgendaTests {
        // 4 testes: sucesso, título duplicado, dados inválidos, idempotência
    }
    
    @Nested 
    @DisplayName("Testes de busca de agenda")
    class GetAgendaTests {
        // 3 testes: busca por ID, não encontrada, listagem paginada
    }
    
    @Nested
    @DisplayName("Testes de atualização de agenda") 
    class UpdateAgendaTests {
        // 4 testes: atualização de status, contadores de votos
    }
    
    @Nested
    @DisplayName("Testes de finalização de agenda")
    class FinalizeAgendaTests {
        // 4 testes: cálculo de resultados, transições de status
    }
}
```

### 2. **IdempotencyServiceTest** (20 testes)
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("IdempotencyService - Testes Unitários")
class IdempotencyServiceTest {
    
    @Nested
    @DisplayName("Testes de geração de chaves")
    class KeyGenerationTests {
        // 3 testes: geração consistente, parâmetros nulos, unicidade
    }
    
    @Nested
    @DisplayName("Testes de armazenamento e recuperação")
    class StorageAndRetrievalTests {
        // 4 testes: cache hit/miss, diferentes tipos de objetos
    }
    
    @Nested
    @DisplayName("Testes de expiração")
    class ExpirationTests {
        // 3 testes: TTL, comportamento temporal
    }
    
    @Nested
    @DisplayName("Testes de thread safety")
    class ThreadSafetyTests {
        // 4 testes: concorrência, operações simultâneas
    }

    @Nested
    @DisplayName("Testes de remoção manual")
    class ManualRemovalTests {
        // 2 testes: remoção específica, chave inexistente
    }

    @Nested
    @DisplayName("Testes de integração e cenários especiais")
    class IntegrationAndSpecialScenariosTests {
        // 4 testes: valores nulos, thread safety, finalização
    }
}
```

### 3. **VoteServiceTest** (8 testes)
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("VoteService - Testes Unitários")
class VoteServiceTest {
    
    @Nested
    @DisplayName("Testes de criação de voto")
    class CreateVoteTests {
        // 5 testes: sucesso, agenda não existe, agenda fechada, usuário já votou
    }
    
    // 3 testes adicionais: busca de votos por usuário e agenda
}
```

### 4. **ExceptionMappingServiceTest** (16 testes)
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("ExceptionMappingService Tests")
class ExceptionMappingServiceTest {
    
    @Nested
    @DisplayName("Testes de Mapeamento de Exceções")
    class ExceptionMappingTests {
        // 4 testes: mapeamento de diferentes tipos de exceções
    }
    
    @Nested
    @DisplayName("Testes de Casos Especiais")
    class SpecialCasesTests {
        // 2 testes: mensagens específicas, mapeamentos customizados
    }
    
    @Nested
    @DisplayName("Testes de Gerenciamento de Mapeamentos")
    class MappingManagementTests {
        // 10 testes: adição, remoção, verificação de mapeamentos
    }
}
```

## Estratégias de Teste Avançadas

### 1. Testes de Concorrência (Thread Safety)

```java
@Test
@DisplayName("Deve manter thread safety")
void deveManterThreadSafety() throws InterruptedException {
    // Arrange
    String keyPrefix = "thread-test-";
    int numberOfThreads = 10;
    int operationsPerThread = 100;

    Thread[] threads = new Thread[numberOfThreads];

    // Act - cria múltiplas threads fazendo operações simultâneas
    for (int i = 0; i < numberOfThreads; i++) {
        final int threadId = i;
        threads[i] = new Thread(() -> {
            for (int j = 0; j < operationsPerThread; j++) {
                String key = keyPrefix + threadId + "-" + j;
                String value = "value-" + threadId + "-" + j;

                idempotencyServiceImpl.storeResult(key, value, 300);
                Result<String> result = idempotencyServiceImpl.checkIdempotency(key);

                assertTrue(result.isSuccess());
                assertEquals(value, result.getValue().get());
            }
        });
    }

    // Inicia todas as threads
    for (Thread thread : threads) {
        thread.start();
    }

    // Aguarda todas terminarem
    for (Thread thread : threads) {
        thread.join();
    }
}
```

### 2. Testes de Integração com Mapeamento de Erros

```java
@Test
@DisplayName("Deve mapear DataIntegrityViolationException para USER_ALREADY_VOTED")
void deveMappearDataIntegrityViolationExceptionParaUserAlreadyVoted() {
    // Arrange
    when(agendaRepository.findById("agenda-123")).thenReturn(Optional.of(agendaEntity));
    when(voteRepository.findByUserIdAndAgendaId("user-456", "agenda-123"))
            .thenReturn(Optional.empty());
    when(userRepository.findById("user-456")).thenReturn(Optional.of(userEntity));
    when(voteMapper.fromCreateRequest(any())).thenReturn(null);
    when(voteMapper.toEntity(any())).thenReturn(voteEntity);

    // Simula violação de constraint única (usuário já votou)
    DataIntegrityViolationException exception = new DataIntegrityViolationException(
            "Duplicate entry for key 'votes_user_id_agenda_id_key'");
    when(voteRepository.save(any())).thenThrow(exception);

    // Act
    Result<VoteResponse> result = voteService.createVote(createVoteRequest);

    // Assert
    assertTrue(result.isError());
    assertEquals("USER_ALREADY_VOTED", result.getErrorCode().orElse(""));
    assertTrue(result.getErrorMessage().orElse("").contains("Duplicate entry"));
}
```

## Métricas de Qualidade

### Matriz de Cobertura por Serviço

| Serviço | Métodos Testados | Cenários de Sucesso | Cenários de Erro | Cenários Especiais | Total |
|---------|------------------|---------------------|-------------------|-------------------|-------|
| **AgendaService** | 4 | 6 | 6 | 3 | **15** |
| **AgendaTimeService** | 3 | 5 | 4 | 3 | **12** |
| **VoteService** | 2 | 3 | 3 | 2 | **8** |
| **VoteServiceIntegration** | 2 | 2 | 2 | 2 | **6** |
| **IdempotencyService** | 6 | 8 | 4 | 8 | **20** |
| **ErrorMappingService** | 3 | 4 | 4 | 4 | **12** |
| **ExceptionMappingService** | 4 | 4 | 4 | 8 | **16** |
| **TOTAL** | **24** | **32** | **27** | **30** | **89** |

### Cobertura por Tipo de Teste

```
Distribuição dos Testes:
├── Cenários de Sucesso (36%): 32 testes
├── Cenários de Erro (30.3%): 27 testes  
├── Cenários Especiais (33.7%): 30 testes
└── Total: 89 testes unitários
```

### Comandos de Teste

```bash
# Executar todos os testes
./mvnw test

# Executar testes com relatório detalhado
./mvnw test -Dtest.verbose=true

# Executar testes em modo silencioso
./mvnw test -q

# Executar testes de um serviço específico
./mvnw test -Dtest=AgendaServiceTest
./mvnw test -Dtest=IdempotencyServiceTest

# Executar com profile de teste
./mvnw test -Dspring.profiles.active=test
``` 