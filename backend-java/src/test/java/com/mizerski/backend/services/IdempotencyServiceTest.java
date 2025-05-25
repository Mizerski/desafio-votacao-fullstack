package com.mizerski.backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mizerski.backend.models.domains.Result;

/**
 * Testes unitários para o serviço de idempotência
 * 
 * Testa todas as funcionalidades do IdempotencyService incluindo cache,
 * expiração e limpeza automática
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("IdempotencyService - Testes Unitários")
class IdempotencyServiceTest {

    private IdempotencyService idempotencyService;

    /**
     * Configuração inicial dos dados de teste
     */
    @BeforeEach
    void setUp() {
        idempotencyService = new IdempotencyService();
        idempotencyService.init(); // Inicializa o serviço
    }

    @Nested
    @DisplayName("Testes de geração de chaves")
    class KeyGenerationTests {

        @Test
        @DisplayName("Deve gerar chave com método e parâmetros")
        void deveGerarChaveComMetodoEParametros() {
            // Arrange
            String methodName = "createUser";
            String param1 = "joao@email.com";
            String param2 = "João Silva";

            // Act
            String key = idempotencyService.generateKey(methodName, param1, param2);

            // Assert
            assertNotNull(key);
            assertEquals("createUser:joao@email.com:João Silva", key);
        }

        @Test
        @DisplayName("Deve gerar chave com parâmetro nulo")
        void deveGerarChaveComParametroNulo() {
            // Arrange
            String methodName = "createAgenda";
            String param1 = "Título da Pauta";
            String param2 = null;

            // Act
            String key = idempotencyService.generateKey(methodName, param1, param2);

            // Assert
            assertNotNull(key);
            assertEquals("createAgenda:Título da Pauta:null", key);
        }

        @Test
        @DisplayName("Deve gerar chave apenas com método")
        void deveGerarChaveApenasComMetodo() {
            // Arrange
            String methodName = "getStats";

            // Act
            String key = idempotencyService.generateKey(methodName);

            // Assert
            assertNotNull(key);
            assertEquals("getStats", key);
        }

        @Test
        @DisplayName("Deve gerar chaves diferentes para parâmetros diferentes")
        void deveGerarChavesDiferentesParaParametrosDiferentes() {
            // Arrange
            String methodName = "createUser";

            // Act
            String key1 = idempotencyService.generateKey(methodName, "user1@email.com");
            String key2 = idempotencyService.generateKey(methodName, "user2@email.com");

            // Assert
            assertNotNull(key1);
            assertNotNull(key2);
            assertFalse(key1.equals(key2));
        }

        @Test
        @DisplayName("Deve gerar mesma chave para mesmos parâmetros")
        void deveGerarMesmaChaveParaMesmosParametros() {
            // Arrange
            String methodName = "createUser";
            String email = "joao@email.com";
            String name = "João Silva";

            // Act
            String key1 = idempotencyService.generateKey(methodName, email, name);
            String key2 = idempotencyService.generateKey(methodName, email, name);

            // Assert
            assertNotNull(key1);
            assertNotNull(key2);
            assertEquals(key1, key2);
        }
    }

    @Nested
    @DisplayName("Testes de armazenamento e recuperação")
    class StorageAndRetrievalTests {

        @Test
        @DisplayName("Deve armazenar e recuperar resultado com sucesso")
        void deveArmazenarERecuperarResultadoComSucesso() {
            // Arrange
            String key = "test-key";
            String result = "test-result";
            int expireAfterSeconds = 300;

            // Act
            idempotencyService.storeResult(key, result, expireAfterSeconds);
            Result<String> retrievedResult = idempotencyService.checkIdempotency(key);

            // Assert
            assertTrue(retrievedResult.isSuccess());
            assertTrue(retrievedResult.getValue().isPresent());
            assertEquals(result, retrievedResult.getValue().get());
        }

        @Test
        @DisplayName("Deve retornar erro quando chave não existe")
        void deveRetornarErroQuandoChaveNaoExiste() {
            // Arrange
            String key = "non-existent-key";

            // Act
            Result<String> result = idempotencyService.checkIdempotency(key);

            // Assert
            assertTrue(result.isError());
            assertTrue(result.getErrorCode().isPresent());
            assertEquals("NOT_FOUND", result.getErrorCode().get());
            assertEquals("Operação não encontrada no cache", result.getErrorMessage().get());
        }

        @Test
        @DisplayName("Deve armazenar diferentes tipos de objetos")
        void deveArmazenarDiferentesTiposDeObjetos() {
            // Arrange
            String stringKey = "string-key";
            String stringValue = "test-string";

            String intKey = "int-key";
            Integer intValue = 42;

            String objectKey = "object-key";
            TestObject objectValue = new TestObject("test", 123);

            // Act
            idempotencyService.storeResult(stringKey, stringValue, 300);
            idempotencyService.storeResult(intKey, intValue, 300);
            idempotencyService.storeResult(objectKey, objectValue, 300);

            Result<String> stringResult = idempotencyService.checkIdempotency(stringKey);
            Result<Integer> intResult = idempotencyService.checkIdempotency(intKey);
            Result<TestObject> objectResult = idempotencyService.checkIdempotency(objectKey);

            // Assert
            assertTrue(stringResult.isSuccess());
            assertEquals(stringValue, stringResult.getValue().get());

            assertTrue(intResult.isSuccess());
            assertEquals(intValue, intResult.getValue().get());

            assertTrue(objectResult.isSuccess());
            assertEquals(objectValue.getName(), objectResult.getValue().get().getName());
            assertEquals(objectValue.getValue(), objectResult.getValue().get().getValue());
        }

        @Test
        @DisplayName("Deve sobrescrever valor existente")
        void deveSobrescreverValorExistente() {
            // Arrange
            String key = "test-key";
            String firstValue = "first-value";
            String secondValue = "second-value";

            // Act
            idempotencyService.storeResult(key, firstValue, 300);
            idempotencyService.storeResult(key, secondValue, 300);

            Result<String> result = idempotencyService.checkIdempotency(key);

            // Assert
            assertTrue(result.isSuccess());
            assertEquals(secondValue, result.getValue().get());
        }
    }

    @Nested
    @DisplayName("Testes de expiração")
    class ExpirationTests {

        @Test
        @DisplayName("Deve retornar sucesso quando entrada não está expirada")
        void deveRetornarSucessoQuandoEntradaNaoEstaExpirada() throws InterruptedException {
            // Arrange
            String key = "valid-key";
            String value = "test-value";
            int expireAfterSeconds = 5; // 5 segundos

            // Act
            idempotencyService.storeResult(key, value, expireAfterSeconds);

            // Aguarda um pouco mas não o suficiente para expirar
            Thread.sleep(500); // 0.5 segundos

            Result<String> result = idempotencyService.checkIdempotency(key);

            // Assert
            assertTrue(result.isSuccess());
            assertEquals(value, result.getValue().get());
        }

        @Test
        @DisplayName("Deve testar comportamento básico de expiração")
        void deveTestarComportamentoBasicoDeExpiracao() {
            // Arrange
            String key = "basic-expire-key";
            String value = "test-value";
            int expireAfterSeconds = 300; // 5 minutos

            // Act
            idempotencyService.storeResult(key, value, expireAfterSeconds);
            Result<String> result = idempotencyService.checkIdempotency(key);

            // Assert - não deve estar expirado imediatamente
            assertTrue(result.isSuccess());
            assertEquals(value, result.getValue().get());
        }

        @Test
        @DisplayName("Deve validar que entrada com tempo longo não expira rapidamente")
        void deveValidarQueEntradaComTempoLongoNaoExpiraRapidamente() throws InterruptedException {
            // Arrange
            String key = "long-expire-key";
            String value = "test-value";
            int expireAfterSeconds = 300; // 5 minutos

            // Act
            idempotencyService.storeResult(key, value, expireAfterSeconds);

            // Aguarda um pouco
            Thread.sleep(100);

            Result<String> result = idempotencyService.checkIdempotency(key);

            // Assert - não deve estar expirado
            assertTrue(result.isSuccess());
            assertEquals(value, result.getValue().get());
        }
    }

    @Nested
    @DisplayName("Testes de remoção manual")
    class ManualRemovalTests {

        @Test
        @DisplayName("Deve remover entrada específica do cache")
        void deveRemoverEntradaEspecificaDoCache() {
            // Arrange
            String key = "test-key";
            String value = "test-value";

            // Act
            idempotencyService.storeResult(key, value, 300);

            // Verifica que está presente
            Result<String> beforeRemoval = idempotencyService.checkIdempotency(key);
            assertTrue(beforeRemoval.isSuccess());

            // Remove do cache
            idempotencyService.removeFromCache(key);

            // Verifica que foi removida
            Result<String> afterRemoval = idempotencyService.checkIdempotency(key);
            assertTrue(afterRemoval.isError());
            assertEquals("NOT_FOUND", afterRemoval.getErrorCode().get());
        }

        @Test
        @DisplayName("Deve lidar com remoção de chave inexistente")
        void deveLidarComRemocaoDeChaveInexistente() {
            // Arrange
            String key = "non-existent-key";

            // Act & Assert - não deve lançar exceção
            idempotencyService.removeFromCache(key);

            // Verifica que continua inexistente
            Result<String> result = idempotencyService.checkIdempotency(key);
            assertTrue(result.isError());
            assertEquals("NOT_FOUND", result.getErrorCode().get());
        }
    }

    @Nested
    @DisplayName("Testes de estatísticas")
    class StatisticsTests {

        @Test
        @DisplayName("Deve retornar estatísticas do cache")
        void deveRetornarEstatisticasDoCache() {
            // Arrange - cache vazio inicialmente
            String initialStats = idempotencyService.getCacheStats();
            assertTrue(initialStats.contains("0 entradas ativas"));

            // Act - adiciona algumas entradas
            idempotencyService.storeResult("key1", "value1", 300);
            idempotencyService.storeResult("key2", "value2", 300);
            idempotencyService.storeResult("key3", "value3", 300);

            String statsWithEntries = idempotencyService.getCacheStats();

            // Assert
            assertTrue(statsWithEntries.contains("3 entradas ativas"));
        }

        @Test
        @DisplayName("Deve atualizar estatísticas após remoções")
        void deveAtualizarEstatisticasAposRemocoes() {
            // Arrange
            idempotencyService.storeResult("key1", "value1", 300);
            idempotencyService.storeResult("key2", "value2", 300);

            String beforeRemoval = idempotencyService.getCacheStats();
            assertTrue(beforeRemoval.contains("2 entradas ativas"));

            // Act
            idempotencyService.removeFromCache("key1");

            String afterRemoval = idempotencyService.getCacheStats();

            // Assert
            assertTrue(afterRemoval.contains("1 entradas ativas"));
        }
    }

    @Nested
    @DisplayName("Testes de integração e cenários especiais")
    class IntegrationAndSpecialScenariosTests {

        @Test
        @DisplayName("Deve lidar com valores nulos")
        void deveLidarComValoresNulos() {
            // Arrange
            String key = "null-value-key";

            // Act
            idempotencyService.storeResult(key, null, 300);
            Result<String> result = idempotencyService.checkIdempotency(key);

            // Assert
            assertTrue(result.isSuccess());
            assertTrue(result.getValue().isEmpty()); // null é tratado como Optional.empty()
        }

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

                        idempotencyService.storeResult(key, value, 300);
                        Result<String> result = idempotencyService.checkIdempotency(key);

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

            // Assert - verifica que todas as operações foram bem-sucedidas
            String finalStats = idempotencyService.getCacheStats();
            assertTrue(finalStats.contains((numberOfThreads * operationsPerThread) + " entradas ativas"));
        }

        @Test
        @DisplayName("Deve testar fluxo completo de idempotência")
        void deveTestarFluxoCompletoDeIdempotencia() {
            // Arrange
            String methodName = "createUser";
            String email = "joao@email.com";
            String name = "João Silva";

            // Act & Assert - Primeira operação
            String key = idempotencyService.generateKey(methodName, email, name);
            Result<String> firstCheck = idempotencyService.checkIdempotency(key);
            assertTrue(firstCheck.isError()); // Não deve existir ainda

            // Simula execução da operação
            String operationResult = "User created with ID: user-123";
            idempotencyService.storeResult(key, operationResult, 300);

            // Segunda tentativa da mesma operação
            Result<String> secondCheck = idempotencyService.checkIdempotency(key);
            assertTrue(secondCheck.isSuccess());
            assertEquals(operationResult, secondCheck.getValue().get());

            // Terceira tentativa - deve retornar o mesmo resultado
            Result<String> thirdCheck = idempotencyService.checkIdempotency(key);
            assertTrue(thirdCheck.isSuccess());
            assertEquals(operationResult, thirdCheck.getValue().get());
        }

        @Test
        @DisplayName("Deve finalizar serviço corretamente")
        void deveFinalizarServicoCorretamente() {
            // Act & Assert - não deve lançar exceção
            idempotencyService.destroy();

            // Verifica que ainda pode ser usado após destroy (para testes)
            String key = "after-destroy-key";
            idempotencyService.storeResult(key, "test", 300);
            Result<String> result = idempotencyService.checkIdempotency(key);
            assertTrue(result.isSuccess());
        }
    }

    /**
     * Classe auxiliar para testes com objetos complexos
     */
    private static class TestObject {
        private final String name;
        private final int value;

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }
}