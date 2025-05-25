package com.mizerski.backend.services;

import java.util.Map;

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
 * Testes para o ExceptionMappingService
 * 
 * Testa o mapeamento de exceções para códigos de erro e funcionalidades
 * de gerenciamento de mapeamentos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExceptionMappingService Tests")
class ExceptionMappingServiceTest {

    private ExceptionMappingService exceptionMappingService;

    @BeforeEach
    void setUp() {
        exceptionMappingService = new ExceptionMappingService();
    }

    @Nested
    @DisplayName("Testes de Mapeamento de Exceções")
    class ExceptionMappingTests {

        @Test
        @DisplayName("Deve mapear NotFoundException para AGENDA_NOT_FOUND")
        void deveMappearNotFoundExceptionParaAgendaNotFound() {
            // Arrange
            Exception exception = new RuntimeException("Agenda não encontrada") {
                @Override
                public String toString() {
                    return "NotFoundException: " + getMessage();
                }

                public String getSimpleName() {
                    return "NotFoundException";
                }
            };

            // Act
            Result<Object> result = exceptionMappingService.mapExceptionToResult(exception);

            // Assert
            assertTrue(result.isError());
            assertEquals("UNKNOWN_ERROR", result.getErrorCode().orElse(""));
            assertEquals("Agenda não encontrada", result.getErrorMessage().orElse(""));
        }

        @Test
        @DisplayName("Deve mapear IllegalArgumentException para INVALID_DATA")
        void deveMappearIllegalArgumentExceptionParaInvalidData() {
            // Arrange
            Exception exception = new IllegalArgumentException("Argumento inválido");

            // Act
            Result<Object> result = exceptionMappingService.mapExceptionToResult(exception);

            // Assert
            assertTrue(result.isError());
            assertEquals("INVALID_DATA", result.getErrorCode().orElse(""));
            assertEquals("Argumento inválido", result.getErrorMessage().orElse(""));
        }

        @Test
        @DisplayName("Deve mapear exceção desconhecida para UNKNOWN_ERROR")
        void deveMappearExcecaoDesconhecidaParaUnknownError() {
            // Arrange
            Exception exception = new RuntimeException("Erro inesperado");

            // Act
            Result<Object> result = exceptionMappingService.mapExceptionToResult(exception);

            // Assert
            assertTrue(result.isError());
            assertEquals("UNKNOWN_ERROR", result.getErrorCode().orElse(""));
            assertEquals("Erro inesperado", result.getErrorMessage().orElse(""));
        }

        @Test
        @DisplayName("Deve lidar com exceção com mensagem nula")
        void deveLidarComExcecaoComMensagemNula() {
            // Arrange
            Exception exception = new RuntimeException((String) null);

            // Act
            Result<Object> result = exceptionMappingService.mapExceptionToResult(exception);

            // Assert
            assertTrue(result.isError());
            assertEquals("UNKNOWN_ERROR", result.getErrorCode().orElse(""));
        }
    }

    @Nested
    @DisplayName("Testes de Casos Especiais")
    class SpecialCasesTests {

        @Test
        @DisplayName("Deve mapear exceções com mensagens específicas corretamente")
        void deveMappearExcecoesComMensagensEspecificasCorretamente() {
            // Test com mensagem de voto duplicado
            Exception voteException = new RuntimeException("Usuário já votou nesta agenda");
            Result<Object> result1 = exceptionMappingService.mapExceptionToResult(voteException);
            assertTrue(result1.isError());
            assertEquals("UNKNOWN_ERROR", result1.getErrorCode().orElse(""));
            assertEquals("Usuário já votou nesta agenda", result1.getErrorMessage().orElse(""));

            // Test com mensagem genérica
            Exception genericException = new RuntimeException("Operação não permitida");
            Result<Object> result2 = exceptionMappingService.mapExceptionToResult(genericException);
            assertTrue(result2.isError());
            assertEquals("UNKNOWN_ERROR", result2.getErrorCode().orElse(""));
            assertEquals("Operação não permitida", result2.getErrorMessage().orElse(""));
        }

        @Test
        @DisplayName("Deve testar adição e remoção de mapeamentos customizados")
        void deveTestarAdicaoERemocaoDeMapeamentosCustomizados() {
            // Arrange
            String customExceptionName = "BadRequestException";
            String customErrorCode = "CUSTOM_BAD_REQUEST";

            // Act - Adicionar mapeamento customizado
            exceptionMappingService.addExceptionMapping(customExceptionName, customErrorCode);

            // Assert - Verificar que foi adicionado
            assertTrue(exceptionMappingService.hasMapping(customExceptionName));

            // Act - Remover mapeamento
            exceptionMappingService.removeExceptionMapping(customExceptionName);

            // Assert - Verificar que foi removido
            assertFalse(exceptionMappingService.hasMapping(customExceptionName));
        }
    }

    @Nested
    @DisplayName("Testes de Gerenciamento de Mapeamentos")
    class MappingManagementTests {

        @Test
        @DisplayName("Deve adicionar novo mapeamento de exceção")
        void deveAdicionarNovoMapeamentoDeExcecao() {
            // Arrange
            String exceptionName = "CustomException";
            String errorCode = "CUSTOM_ERROR";

            // Act
            exceptionMappingService.addExceptionMapping(exceptionName, errorCode);

            // Assert
            assertTrue(exceptionMappingService.hasMapping(exceptionName));
        }

        @Test
        @DisplayName("Deve remover mapeamento de exceção existente")
        void deveRemoverMapeamentoDeExcecaoExistente() {
            // Arrange
            String exceptionName = "ValidationException";
            assertTrue(exceptionMappingService.hasMapping(exceptionName));

            // Act
            exceptionMappingService.removeExceptionMapping(exceptionName);

            // Assert
            assertFalse(exceptionMappingService.hasMapping(exceptionName));
        }

        @Test
        @DisplayName("Deve verificar se existe mapeamento para exceção")
        void deveVerificarSeExisteMapeamentoParaExcecao() {
            // Assert - Mapeamentos existentes
            assertTrue(exceptionMappingService.hasMapping("NotFoundException"));
            assertTrue(exceptionMappingService.hasMapping("ValidationException"));
            assertTrue(exceptionMappingService.hasMapping("ConflictException"));

            // Assert - Mapeamentos inexistentes
            assertFalse(exceptionMappingService.hasMapping("NonExistentException"));
            assertFalse(exceptionMappingService.hasMapping("CustomException"));
        }

        @Test
        @DisplayName("Deve obter todos os mapeamentos configurados")
        void deveObterTodosOsMapeamentosConfigurados() {
            // Act
            Map<String, String> mappings = exceptionMappingService.getAllMappings();

            // Assert
            assertNotNull(mappings);
            assertFalse(mappings.isEmpty());

            // Verificar alguns mapeamentos esperados
            assertEquals("AGENDA_NOT_FOUND", mappings.get("NotFoundException"));
            assertEquals("AGENDA_NOT_FOUND", mappings.get("AgendaNotFoundException"));
            assertEquals("USER_NOT_FOUND", mappings.get("UserNotFoundException"));
            assertEquals("VOTE_NOT_FOUND", mappings.get("VoteNotFoundException"));
            assertEquals("INVALID_DATA", mappings.get("ValidationException"));
            assertEquals("INVALID_DATA", mappings.get("IllegalArgumentException"));
            assertEquals("DUPLICATE_RESOURCE", mappings.get("ConflictException"));
            assertEquals("DUPLICATE_RESOURCE", mappings.get("DuplicateKeyException"));
            assertEquals("OPERATION_NOT_ALLOWED", mappings.get("BusinessRuleException"));
            assertEquals("AGENDA_NOT_OPEN", mappings.get("AgendaNotOpenException"));
        }

        @Test
        @DisplayName("Deve retornar cópia dos mapeamentos para evitar modificação externa")
        void deveRetornarCopiaDosMapeamentosParaEvitarModificacaoExterna() {
            // Act
            Map<String, String> mappings = exceptionMappingService.getAllMappings();
            int originalSize = mappings.size();

            // Tentar modificar o mapa retornado
            mappings.put("TestException", "TEST_ERROR");

            // Assert - Verificar que o mapeamento interno não foi afetado
            Map<String, String> newMappings = exceptionMappingService.getAllMappings();
            assertEquals(originalSize, newMappings.size());
            assertFalse(newMappings.containsKey("TestException"));
        }

        @Test
        @DisplayName("Deve sobrescrever mapeamento existente ao adicionar novo")
        void deveSobrescreverMapeamentoExistenteAoAdicionarNovo() {
            // Arrange
            String exceptionName = "ValidationException";
            String newErrorCode = "VALIDATION_FAILED";

            // Verificar mapeamento original
            assertTrue(exceptionMappingService.hasMapping(exceptionName));

            // Act - Sobrescrever mapeamento
            exceptionMappingService.addExceptionMapping(exceptionName, newErrorCode);

            // Assert - Verificar que ainda existe
            assertTrue(exceptionMappingService.hasMapping(exceptionName));
        }
    }

    @Nested
    @DisplayName("Testes de Integração e Cenários Especiais")
    class IntegrationAndSpecialScenariosTests {

        @Test
        @DisplayName("Deve manter consistência após múltiplas operações")
        void deveManterConsistenciaAposMultiplasOperacoes() {
            // Arrange
            String customException = "CustomException";
            String customErrorCode = "CUSTOM_ERROR";

            // Act & Assert - Adicionar mapeamento
            exceptionMappingService.addExceptionMapping(customException, customErrorCode);
            assertTrue(exceptionMappingService.hasMapping(customException));

            // Act & Assert - Remover mapeamento
            exceptionMappingService.removeExceptionMapping(customException);
            assertFalse(exceptionMappingService.hasMapping(customException));
        }

        @Test
        @DisplayName("Deve lidar com operações em mapeamentos inexistentes")
        void deveLidarComOperacoesEmMapeamentosInexistentes() {
            // Arrange
            String inexistentException = "InexistentException";

            // Act & Assert - Verificar que não existe
            assertFalse(exceptionMappingService.hasMapping(inexistentException));

            // Act & Assert - Remover mapeamento inexistente (não deve causar erro)
            exceptionMappingService.removeExceptionMapping(inexistentException);
            assertFalse(exceptionMappingService.hasMapping(inexistentException));
        }

        @Test
        @DisplayName("Deve mapear diferentes tipos de exceções corretamente")
        void deveMappearDiferentesTiposDeExcecoesCorretamente() {
            // Test NullPointerException
            Exception nullPointer = new NullPointerException("Valor nulo");
            Result<Object> result1 = exceptionMappingService.mapExceptionToResult(nullPointer);
            assertEquals("UNKNOWN_ERROR", result1.getErrorCode().orElse(""));

            // Test IllegalArgumentException
            Exception illegalArg = new IllegalArgumentException("Argumento inválido");
            Result<Object> result2 = exceptionMappingService.mapExceptionToResult(illegalArg);
            assertEquals("INVALID_DATA", result2.getErrorCode().orElse(""));

            // Test RuntimeException genérica
            Exception runtime = new RuntimeException("Erro genérico");
            Result<Object> result3 = exceptionMappingService.mapExceptionToResult(runtime);
            assertEquals("UNKNOWN_ERROR", result3.getErrorCode().orElse(""));
        }
    }
}