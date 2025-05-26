package com.mizerski.backend.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários para o serviço de tratamento de exceções JWT.
 * Valida o mapeamento correto de exceções para handlers específicos.
 */
class JwtExceptionHandlerServiceTest {

    private JwtExceptionHandlerService jwtExceptionHandlerService;

    @BeforeEach
    void setUp() {
        jwtExceptionHandlerService = new JwtExceptionHandlerService();
    }

    @Test
    void deveRetornarTrueParaExcecaoIllegalArgumentException() {
        // Arrange
        Exception exception = new IllegalArgumentException("Invalid argument");

        // Act
        boolean handled = jwtExceptionHandlerService.handleJwtException(exception, null);

        // Assert
        assertTrue(handled);
    }

    @Test
    void deveRetornarTrueParaExcecaoSecurityException() {
        // Arrange
        Exception exception = new SecurityException("Invalid signature");

        // Act
        boolean handled = jwtExceptionHandlerService.handleJwtException(exception, null);

        // Assert
        assertTrue(handled);
    }

    @Test
    void deveRetornarFalseParaExcecaoNaoMapeada() {
        // Arrange
        Exception exception = new RuntimeException("Unknown error");

        // Act
        boolean handled = jwtExceptionHandlerService.handleJwtException(exception, null);

        // Assert
        assertFalse(handled);
    }

    @Test
    void deveVerificarSeTemHandlerParaExcecaoEspecifica() {
        // Act & Assert
        assertTrue(jwtExceptionHandlerService.hasHandler("ExpiredJwtException"));
        assertTrue(jwtExceptionHandlerService.hasHandler("MalformedJwtException"));
        assertTrue(jwtExceptionHandlerService.hasHandler("UnsupportedJwtException"));
        assertTrue(jwtExceptionHandlerService.hasHandler("SecurityException"));
        assertTrue(jwtExceptionHandlerService.hasHandler("IllegalArgumentException"));
        assertFalse(jwtExceptionHandlerService.hasHandler("UnknownException"));
    }

    @Test
    void devePermitirAdicionarNovoHandler() {
        // Arrange
        String exceptionName = "CustomJwtException";

        // Act
        jwtExceptionHandlerService.addExceptionHandler(exceptionName, (userEmail, message) -> {
            // Custom handler logic
        });

        // Assert
        assertTrue(jwtExceptionHandlerService.hasHandler(exceptionName));
    }

    @Test
    void devePermitirRemoverHandler() {
        // Arrange
        String exceptionName = "ExpiredJwtException";
        assertTrue(jwtExceptionHandlerService.hasHandler(exceptionName));

        // Act
        jwtExceptionHandlerService.removeExceptionHandler(exceptionName);

        // Assert
        assertFalse(jwtExceptionHandlerService.hasHandler(exceptionName));
    }

    @Test
    void deveRetornarTodosOsHandlersConfigurados() {
        // Act
        var handlers = jwtExceptionHandlerService.getAllHandlers();

        // Assert
        assertTrue(handlers.containsKey("ExpiredJwtException"));
        assertTrue(handlers.containsKey("MalformedJwtException"));
        assertTrue(handlers.containsKey("UnsupportedJwtException"));
        assertTrue(handlers.containsKey("SecurityException"));
        assertTrue(handlers.containsKey("JwtException"));
        assertTrue(handlers.containsKey("IllegalArgumentException"));
    }

}