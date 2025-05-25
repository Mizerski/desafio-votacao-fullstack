package com.mizerski.backend.services;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.services.ErrorMappingService.ErrorResponse;

/**
 * Testes para o ErrorMappingService
 * 
 * Testa o mapeamento de códigos de erro para ResponseEntity e funcionalidades
 * de criação de respostas de erro
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ErrorMappingService Tests")
class ErrorMappingServiceTest {

    private ErrorMappingService errorMappingService;

    @BeforeEach
    void setUp() {
        errorMappingService = new ErrorMappingService();
    }

    @Nested
    @DisplayName("Testes de Mapeamento de Result para ResponseEntity")
    class ResultMappingTests {

        @Test
        @DisplayName("Deve mapear erro NOT_FOUND para ResponseEntity 404 sem body")
        void deveMappearErroNotFoundParaResponseEntity404SemBody() {
            // Arrange
            Result<String> result = Result.error("AGENDA_NOT_FOUND", "Agenda não encontrada");

            // Act
            ResponseEntity<String> response = errorMappingService.mapErrorToResponse(result);

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertFalse(response.hasBody());
        }

        @Test
        @DisplayName("Deve mapear erro BAD_REQUEST para ResponseEntity 400 com body")
        void deveMappearErroBadRequestParaResponseEntity400ComBody() {
            // Arrange
            Result<Object> result = Result.error("INVALID_TITLE", "Dados inválidos");

            // Act
            ResponseEntity<Object> response = errorMappingService.mapErrorToResponse(result);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.hasBody());
            assertNotNull(response.getBody());
        }

        @Test
        @DisplayName("Deve mapear erro CONFLICT para ResponseEntity 409 com body")
        void deveMappearErroConflictParaResponseEntity409ComBody() {
            // Arrange
            Result<Object> result = Result.error("USER_ALREADY_VOTED", "Usuário já votou");

            // Act
            ResponseEntity<Object> response = errorMappingService.mapErrorToResponse(result);

            // Assert
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertTrue(response.hasBody());

            ErrorResponse errorResponse = (ErrorResponse) response.getBody();
            assertNotNull(errorResponse);
            assertEquals("USER_ALREADY_VOTED", errorResponse.errorCode);
            assertEquals("Usuário já votou", errorResponse.message);
        }

        @Test
        @DisplayName("Deve mapear erro UNPROCESSABLE_ENTITY para ResponseEntity 422 com body")
        void deveMappearErroUnprocessableEntityParaResponseEntity422ComBody() {
            // Arrange
            Result<Object> result = Result.error("AGENDA_NOT_OPEN", "Agenda não está aberta");

            // Act
            ResponseEntity<Object> response = errorMappingService.mapErrorToResponse(result);

            // Assert
            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertTrue(response.hasBody());

            ErrorResponse errorResponse = (ErrorResponse) response.getBody();
            assertNotNull(errorResponse);
            assertEquals("AGENDA_NOT_OPEN", errorResponse.errorCode);
            assertEquals("Agenda não está aberta", errorResponse.message);
        }

        @Test
        @DisplayName("Deve mapear erro UNKNOWN_ERROR para ResponseEntity 500 com body")
        void deveMappearErroUnknownErrorParaResponseEntity500ComBody() {
            // Arrange
            Result<Object> result = Result.error("UNKNOWN_ERROR", "Erro desconhecido");

            // Act
            ResponseEntity<Object> response = errorMappingService.mapErrorToResponse(result);

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertTrue(response.hasBody());

            ErrorResponse errorResponse = (ErrorResponse) response.getBody();
            assertNotNull(errorResponse);
            assertEquals("UNKNOWN_ERROR", errorResponse.errorCode);
            assertEquals("Erro desconhecido", errorResponse.message);
        }

        @Test
        @DisplayName("Deve usar mensagem padrão quando Result não tem mensagem")
        void deveUsarMensagemPadraoQuandoResultNaoTemMensagem() {
            // Arrange
            Result<Object> result = Result.error("INVALID_TITLE", null);

            // Act
            ResponseEntity<Object> response = errorMappingService.mapErrorToResponse(result);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.hasBody());

            ErrorResponse errorResponse = (ErrorResponse) response.getBody();
            assertNotNull(errorResponse);
            assertEquals("INVALID_TITLE", errorResponse.errorCode);
            assertEquals("Título inválido", errorResponse.message); // Mensagem padrão do enum
        }

        @Test
        @DisplayName("Deve lançar exceção quando Result é de sucesso")
        void deveLancarExcecaoQuandoResultEhDeSucesso() {
            // Arrange
            Result<String> result = Result.success("Sucesso");

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> errorMappingService.mapErrorToResponse(result));

            assertEquals("Result deve conter erro para ser mapeado", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes de Criação Direta de ErrorResponse")
    class DirectErrorResponseTests {

        @Test
        @DisplayName("Deve criar ErrorResponse com mensagem customizada")
        void deveCriarErrorResponseComMensagemCustomizada() {
            // Arrange
            String errorCode = "INVALID_TITLE";
            String customMessage = "Mensagem customizada";

            // Act
            ResponseEntity<ErrorResponse> response = errorMappingService.createErrorResponse(errorCode, customMessage);

            // Assert
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.hasBody());

            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(errorCode, errorResponse.errorCode);
            assertEquals(customMessage, errorResponse.message);
            assertNotNull(errorResponse.timestamp);
        }

        @Test
        @DisplayName("Deve usar mensagem padrão quando mensagem customizada é nula")
        void deveUsarMensagemPadraoQuandoMensagemCustomizadaEhNula() {
            // Arrange
            String errorCode = "AGENDA_NOT_FOUND";

            // Act
            ResponseEntity<ErrorResponse> response = errorMappingService.createErrorResponse(errorCode, null);

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.hasBody());

            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(errorCode, errorResponse.errorCode);
            assertEquals("Agenda não encontrada", errorResponse.message); // Mensagem padrão
        }

        @Test
        @DisplayName("Deve usar mensagem padrão quando mensagem customizada é vazia")
        void deveUsarMensagemPadraoQuandoMensagemCustomizadaEhVazia() {
            // Arrange
            String errorCode = "USER_NOT_FOUND";
            String emptyMessage = "   ";

            // Act
            ResponseEntity<ErrorResponse> response = errorMappingService.createErrorResponse(errorCode, emptyMessage);

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertTrue(response.hasBody());

            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(errorCode, errorResponse.errorCode);
            assertEquals("Usuário não encontrado", errorResponse.message); // Mensagem padrão
        }

        @Test
        @DisplayName("Deve criar ErrorResponse para código de erro desconhecido")
        void deveCriarErrorResponseParaCodigoDeErroDesconhecido() {
            // Arrange
            String unknownErrorCode = "CUSTOM_ERROR";
            String customMessage = "Erro customizado";

            // Act
            ResponseEntity<ErrorResponse> response = errorMappingService.createErrorResponse(unknownErrorCode,
                    customMessage);

            // Assert
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()); // Default para unknown
            assertTrue(response.hasBody());

            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertEquals(unknownErrorCode, errorResponse.errorCode);
            assertEquals(customMessage, errorResponse.message);
        }
    }

    @Nested
    @DisplayName("Testes de Verificação de Status Only")
    class StatusOnlyTests {

        @Test
        @DisplayName("Deve retornar true para códigos de erro NOT_FOUND")
        void deveRetornarTrueParaCodigosDeErroNotFound() {
            // Assert
            assertTrue(errorMappingService.shouldReturnStatusOnly("AGENDA_NOT_FOUND"));
            assertTrue(errorMappingService.shouldReturnStatusOnly("USER_NOT_FOUND"));
            assertTrue(errorMappingService.shouldReturnStatusOnly("VOTE_NOT_FOUND"));
        }

        @Test
        @DisplayName("Deve retornar false para códigos de erro que não são NOT_FOUND")
        void deveRetornarFalseParaCodigosDeErroQueNaoSaoNotFound() {
            // Assert
            assertFalse(errorMappingService.shouldReturnStatusOnly("INVALID_TITLE"));
            assertFalse(errorMappingService.shouldReturnStatusOnly("USER_ALREADY_VOTED"));
            assertFalse(errorMappingService.shouldReturnStatusOnly("AGENDA_NOT_OPEN"));
            assertFalse(errorMappingService.shouldReturnStatusOnly("UNKNOWN_ERROR"));
        }

        @Test
        @DisplayName("Deve retornar false para código de erro nulo")
        void deveRetornarFalseParaCodigoDeErroNulo() {
            // Act & Assert
            assertFalse(errorMappingService.shouldReturnStatusOnly(null));
        }

        @Test
        @DisplayName("Deve retornar false para código de erro desconhecido")
        void deveRetornarFalseParaCodigoDeErroDesconhecido() {
            // Act & Assert
            assertFalse(errorMappingService.shouldReturnStatusOnly("CUSTOM_UNKNOWN_ERROR"));
        }
    }

    @Nested
    @DisplayName("Testes da Classe ErrorResponse")
    class ErrorResponseTests {

        @Test
        @DisplayName("Deve criar ErrorResponse com todos os campos")
        void deveCriarErrorResponseComTodosOsCampos() {
            // Arrange
            String errorCode = "TEST_ERROR";
            String message = "Mensagem de teste";
            LocalDateTime timestamp = LocalDateTime.now();

            // Act
            ErrorResponse errorResponse = new ErrorResponse(errorCode, message, timestamp);

            // Assert
            assertEquals(errorCode, errorResponse.errorCode);
            assertEquals(message, errorResponse.message);
            assertEquals(timestamp, errorResponse.timestamp);
        }

        @Test
        @DisplayName("Deve permitir campos nulos na ErrorResponse")
        void devePermitirCamposNulosNaErrorResponse() {
            // Act
            ErrorResponse errorResponse = new ErrorResponse(null, null, null);

            // Assert - Não deve lançar exceção
            assertNotNull(errorResponse);
        }
    }

    @Nested
    @DisplayName("Testes de Integração e Cenários Especiais")
    class IntegrationAndSpecialScenariosTests {

        @Test
        @DisplayName("Deve manter consistência entre diferentes métodos")
        void deveManterConsistenciaEntreDiferentesMetodos() {
            // Arrange
            String errorCode = "INVALID_TITLE";
            String message = "Dados inválidos";
            Result<Object> result = Result.error(errorCode, message);

            // Act
            ResponseEntity<Object> responseFromResult = errorMappingService.mapErrorToResponse(result);
            ResponseEntity<ErrorResponse> responseFromDirect = errorMappingService.createErrorResponse(errorCode,
                    message);
            boolean statusOnly = errorMappingService.shouldReturnStatusOnly(errorCode);

            // Assert
            assertEquals(responseFromResult.getStatusCode(), responseFromDirect.getStatusCode());
            assertFalse(statusOnly); // INVALID_TITLE não é NOT_FOUND

            if (responseFromResult.hasBody() && responseFromDirect.hasBody()) {
                ErrorResponse errorFromResult = (ErrorResponse) responseFromResult.getBody();
                ErrorResponse errorFromDirect = responseFromDirect.getBody();

                assertEquals(errorFromResult.errorCode, errorFromDirect.errorCode);
                assertEquals(errorFromResult.message, errorFromDirect.message);
            }
        }

        @Test
        @DisplayName("Deve lidar com diferentes tipos de Result")
        void deveLidarComDiferentesTiposDeResult() {
            // Test com Result<Object>
            Result<Object> stringResult = Result.error("INVALID_TITLE", "Erro string");
            ResponseEntity<Object> stringResponse = errorMappingService.mapErrorToResponse(stringResult);
            assertEquals(HttpStatus.BAD_REQUEST, stringResponse.getStatusCode());

            // Test com Result<Object>
            Result<Object> objectResult = Result.error("USER_NOT_FOUND", "Erro object");
            ResponseEntity<Object> objectResponse = errorMappingService.mapErrorToResponse(objectResult);
            assertEquals(HttpStatus.NOT_FOUND, objectResponse.getStatusCode());

            // Test com Result<Object>
            Result<Object> intResult = Result.error("UNKNOWN_ERROR", "Erro integer");
            ResponseEntity<Object> intResponse = errorMappingService.mapErrorToResponse(intResult);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, intResponse.getStatusCode());
        }

        @Test
        @DisplayName("Deve validar timestamp em ErrorResponse")
        void deveValidarTimestampEmErrorResponse() {
            // Arrange
            LocalDateTime before = LocalDateTime.now();

            // Act
            ResponseEntity<ErrorResponse> response = errorMappingService.createErrorResponse("INVALID_TITLE", "Teste");

            // Assert
            LocalDateTime after = LocalDateTime.now();
            ErrorResponse errorResponse = response.getBody();
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.timestamp);

            // Verificar que o timestamp está dentro do intervalo esperado
            assertTrue(errorResponse.timestamp.isAfter(before.minusSeconds(1)));
            assertTrue(errorResponse.timestamp.isBefore(after.plusSeconds(1)));
        }
    }
}