package com.mizerski.backend.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mizerski.backend.dtos.request.LoginRequest;
import com.mizerski.backend.dtos.request.RegisterRequest;
import com.mizerski.backend.dtos.response.AuthResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.entities.UserEntity;
import com.mizerski.backend.models.enums.UserRole;
import com.mizerski.backend.repositories.UserRepository;

/**
 * Testes unitários para o serviço de autenticação.
 * Demonstra o funcionamento do Result Pattern e validações.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private JwtService jwtService;

        @Mock
        private AuthenticationManager authenticationManager;

        @Mock
        private ExceptionMappingService exceptionMappingService;

        @Mock
        private Authentication authentication;

        private AuthServiceImpl authService;

        @BeforeEach
        void setUp() {
                authService = new AuthServiceImpl(
                                userRepository,
                                passwordEncoder,
                                jwtService,
                                authenticationManager,
                                exceptionMappingService);
        }

        @Test
        void testLoginSuccess() {
                // Arrange
                LoginRequest request = LoginRequest.builder()
                                .email("test@example.com")
                                .password("password123")
                                .build();

                UserEntity user = UserEntity.builder()
                                .id("user-id")
                                .name("Test User")
                                .email("test@example.com")
                                .password("encoded-password")
                                .role(UserRole.USER)
                                .isActive(true)
                                .build();

                when(userRepository.findByEmailAndIsActiveTrue(anyString()))
                                .thenReturn(Optional.of(user));
                when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                                .thenReturn(authentication);
                when(authentication.isAuthenticated()).thenReturn(true);
                when(userRepository.save(any(UserEntity.class))).thenReturn(user);
                when(jwtService.generateToken(any(UserEntity.class))).thenReturn("access-token");
                when(jwtService.getExpirationTime()).thenReturn(86400L);

                // Act
                Result<AuthResponse> result = authService.login(request);

                // Assert
                assertTrue(result.isSuccess());
                assertNotNull(result.getValue().orElse(null));

                AuthResponse response = result.getValue().orElse(null);
                assertEquals("access-token", response.getToken());
                assertEquals("Bearer", response.getTokenType());
                assertEquals(86400L, response.getExpiresIn());
                assertNotNull(response.getUser());
                assertEquals("test@example.com", response.getUser().getEmail());
        }

        @Test
        void testLoginInvalidCredentials() {
                // Arrange
                LoginRequest request = LoginRequest.builder()
                                .email("test@example.com")
                                .password("wrong-password")
                                .build();

                when(userRepository.findByEmailAndIsActiveTrue(anyString()))
                                .thenReturn(Optional.empty());

                // Act
                Result<AuthResponse> result = authService.login(request);

                // Assert
                assertTrue(result.isError());
                assertEquals("INVALID_CREDENTIALS", result.getErrorCode().orElse(null));
                assertEquals("Email ou senha inválidos", result.getErrorMessage().orElse(null));
        }

        @Test
        void testRegisterSuccess() {
                // Arrange
                RegisterRequest request = RegisterRequest.builder()
                                .name("New User")
                                .email("newuser@example.com")
                                .password("password123")
                                .role(UserRole.USER)
                                .build();

                UserEntity savedUser = UserEntity.builder()
                                .id("new-user-id")
                                .name("New User")
                                .email("newuser@example.com")
                                .password("encoded-password")
                                .role(UserRole.USER)
                                .isActive(true)
                                .build();

                when(userRepository.existsByEmail(anyString())).thenReturn(false);
                when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
                when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
                when(jwtService.generateToken(any(UserEntity.class))).thenReturn("access-token");
                when(jwtService.getExpirationTime()).thenReturn(86400L);

                // Act
                Result<AuthResponse> result = authService.register(request);

                // Assert
                assertTrue(result.isSuccess());
                assertNotNull(result.getValue().orElse(null));

                AuthResponse response = result.getValue().orElse(null);
                assertEquals("access-token", response.getToken());
                assertEquals("newuser@example.com", response.getUser().getEmail());
        }

        @Test
        void testRegisterDuplicateEmail() {
                // Arrange
                RegisterRequest request = RegisterRequest.builder()
                                .name("New User")
                                .email("existing@example.com")
                                .password("password123")
                                .build();

                when(userRepository.existsByEmail(anyString())).thenReturn(true);

                // Act
                Result<AuthResponse> result = authService.register(request);

                // Assert
                assertTrue(result.isError());
                assertEquals("DUPLICATE_EMAIL", result.getErrorCode().orElse(null));
                assertEquals("Email já cadastrado", result.getErrorMessage().orElse(null));
        }

        @Test
        void testValidateTokenSuccess() {
                // Arrange
                String token = "valid-token";
                UserEntity user = UserEntity.builder()
                                .email("test@example.com")
                                .isActive(true)
                                .build();

                when(jwtService.extractUsername(token)).thenReturn("test@example.com");
                when(userRepository.findByEmailAndIsActiveTrue("test@example.com"))
                                .thenReturn(Optional.of(user));
                when(jwtService.isTokenValid(token, user)).thenReturn(true);

                // Act
                Result<Boolean> result = authService.validateToken(token);

                // Assert
                assertTrue(result.isSuccess());
                assertTrue(result.getValue().orElse(false));
        }

        @Test
        void testValidateTokenInvalid() {
                // Arrange
                String token = "invalid-token";

                when(jwtService.extractUsername(token)).thenReturn(null);

                // Act
                Result<Boolean> result = authService.validateToken(token);

                // Assert
                assertTrue(result.isSuccess());
                assertEquals(false, result.getValue().orElse(true));
        }
}