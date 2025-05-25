package com.mizerski.backend.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mizerski.backend.models.entities.UserEntity;
import com.mizerski.backend.models.enums.UserRole;
import com.mizerski.backend.repositories.UserRepository;

/**
 * Testes unitários para o serviço customizado de detalhes do usuário.
 * Valida carregamento de usuários para autenticação Spring Security.
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        // Arrange
        String email = "test@example.com";
        UserEntity user = UserEntity.builder()
                .id("user-id")
                .name("Test User")
                .email(email)
                .password("encoded-password")
                .role(UserRole.USER)
                .isActive(true)
                .isEmailVerified(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        when(userRepository.findByEmailAndIsActiveTrue(email))
                .thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("encoded-password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());

        // Verifica se o método do repositório foi chamado
        verify(userRepository).findByEmailAndIsActiveTrue(email);
    }

    @Test
    void testLoadUserByUsernameUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";

        when(userRepository.findByEmailAndIsActiveTrue(email))
                .thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email));

        assertEquals("Usuário não encontrado: " + email, exception.getMessage());
        verify(userRepository).findByEmailAndIsActiveTrue(email);
    }

    @Test
    void testLoadUserByUsernameWithAdminRole() {
        // Arrange
        String email = "admin@example.com";
        UserEntity adminUser = UserEntity.builder()
                .id("admin-id")
                .name("Admin User")
                .email(email)
                .password("admin-password")
                .role(UserRole.ADMIN)
                .isActive(true)
                .isEmailVerified(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        when(userRepository.findByEmailAndIsActiveTrue(email))
                .thenReturn(Optional.of(adminUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testLoadUserByUsernameWithInactiveUser() {
        // Arrange
        String email = "inactive@example.com";

        // Usuário inativo não será retornado pelo método findByEmailAndIsActiveTrue
        when(userRepository.findByEmailAndIsActiveTrue(email))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email));
    }

    @Test
    void testLoadUserByUsernameWithNullEmail() {
        // Arrange
        String nullEmail = null;

        when(userRepository.findByEmailAndIsActiveTrue(nullEmail))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(nullEmail));
    }

    @Test
    void testLoadUserByUsernameWithEmptyEmail() {
        // Arrange
        String emptyEmail = "";

        when(userRepository.findByEmailAndIsActiveTrue(emptyEmail))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(emptyEmail));
    }

    @Test
    void testUserDetailsImplementsCorrectInterface() {
        // Arrange
        String email = "test@example.com";
        UserEntity user = UserEntity.builder()
                .id("user-id")
                .name("Test User")
                .email(email)
                .password("encoded-password")
                .role(UserRole.USER)
                .isActive(true)
                .build();

        when(userRepository.findByEmailAndIsActiveTrue(email))
                .thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertTrue(userDetails instanceof UserEntity);
        assertTrue(userDetails instanceof UserDetails);
    }

    @Test
    void testRepositoryMethodCalledWithCorrectParameter() {
        // Arrange
        String email = "specific@example.com";

        when(userRepository.findByEmailAndIsActiveTrue(anyString()))
                .thenReturn(Optional.empty());

        // Act
        try {
            userDetailsService.loadUserByUsername(email);
        } catch (UsernameNotFoundException e) {
            // Esperado
        }

        // Assert
        verify(userRepository).findByEmailAndIsActiveTrue(email);
    }

    @Test
    void testUserWithAllAccountFlags() {
        // Arrange
        String email = "complete@example.com";
        UserEntity user = UserEntity.builder()
                .id("user-id")
                .name("Complete User")
                .email(email)
                .password("password")
                .role(UserRole.USER)
                .isActive(true)
                .isEmailVerified(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        when(userRepository.findByEmailAndIsActiveTrue(email))
                .thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }
}