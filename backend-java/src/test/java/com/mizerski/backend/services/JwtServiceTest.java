package com.mizerski.backend.services;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.mizerski.backend.models.entities.UserEntity;
import com.mizerski.backend.models.enums.UserRole;

/**
 * Testes unitários para o serviço JWT.
 * Valida geração, validação e extração de claims dos tokens.
 */
class JwtServiceTest {

    private JwtServiceImpl jwtService;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();

        // Configura propriedades para teste
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L); // 24 horas
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604800000L); // 7 dias

        // Cria usuário de teste
        testUser = UserEntity.builder()
                .id("test-user-id")
                .name("Test User")
                .email("test@example.com")
                .password("encoded-password")
                .role(UserRole.USER)
                .isActive(true)
                .build();
    }

    @Test
    void testGenerateToken() {
        // Act
        String token = jwtService.generateToken(testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT tem 3 partes separadas por ponto
    }

    @Test
    void testGenerateTokenWithExtraClaims() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("customClaim", "customValue");

        // Act
        String token = jwtService.generateToken(extraClaims, testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGenerateRefreshToken() {
        // Act
        String refreshToken = jwtService.generateRefreshToken(testUser);

        // Assert
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertTrue(refreshToken.split("\\.").length == 3);
    }

    @Test
    void testExtractUsername() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertEquals("test@example.com", extractedUsername);
    }

    @Test
    void testExtractUserId() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        String extractedUserId = jwtService.extractUserId(token);

        // Assert
        assertEquals("test-user-id", extractedUserId);
    }

    @Test
    void testExtractUserRole() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        String extractedRole = jwtService.extractUserRole(token);

        // Assert
        assertEquals("USER", extractedRole);
    }

    @Test
    void testIsTokenValid() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        boolean isValid = jwtService.isTokenValid(token, testUser);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testIsTokenValidWithDifferentUser() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        UserEntity differentUser = UserEntity.builder()
                .email("different@example.com")
                .build();

        // Act
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testIsRefreshTokenValid() {
        // Arrange
        String refreshToken = jwtService.generateRefreshToken(testUser);

        // Act
        boolean isValid = jwtService.isRefreshTokenValid(refreshToken, testUser);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testIsTokenExpired() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        boolean isExpired = jwtService.isTokenExpired(token);

        // Assert
        assertFalse(isExpired); // Token recém criado não deve estar expirado
    }

    @Test
    void testGetExpirationTime() {
        // Act
        long expirationTime = jwtService.getExpirationTime();

        // Assert
        assertEquals(86400L, expirationTime); // 24 horas em segundos
    }

    @Test
    void testBuildTokenWithNullUserDetails() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.generateToken(null);
        });
    }

    @Test
    void testBuildTokenWithNullUsername() {
        // Arrange
        UserEntity userWithNullEmail = UserEntity.builder()
                .id("test-id")
                .name("Test User")
                .email(null) // Username será null
                .role(UserRole.USER)
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.generateToken(userWithNullEmail);
        });
    }

    @Test
    void testIsTokenValidWithInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtService.isTokenValid(invalidToken, testUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testIsRefreshTokenValidWithInvalidToken() {
        // Arrange
        String invalidRefreshToken = "invalid.refresh.token";

        // Act
        boolean isValid = jwtService.isRefreshTokenValid(invalidRefreshToken, testUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testTokenContainsUserClaims() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        String userId = jwtService.extractUserId(token);
        String userRole = jwtService.extractUserRole(token);
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals("test-user-id", userId);
        assertEquals("USER", userRole);
        assertEquals("test@example.com", username);
    }

    @Test
    void testAdminUserToken() {
        // Arrange
        UserEntity adminUser = UserEntity.builder()
                .id("admin-id")
                .name("Admin User")
                .email("admin@example.com")
                .role(UserRole.ADMIN)
                .build();

        // Act
        String token = jwtService.generateToken(adminUser);
        String extractedRole = jwtService.extractUserRole(token);

        // Assert
        assertEquals("ADMIN", extractedRole);
    }
}