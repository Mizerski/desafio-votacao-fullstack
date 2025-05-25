package com.mizerski.backend.dtos.response;

import java.time.LocalDateTime;

import com.mizerski.backend.models.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de autenticação contendo token JWT e dados do usuário.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tokenType;
    private Long expiresIn;
    private UserInfo user;

    /**
     * Informações básicas do usuário autenticado
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String id;
        private String name;
        private String email;
        private UserRole role;
        private Boolean isActive;
        private Boolean isEmailVerified;
        private LocalDateTime lastLogin;
    }

    /**
     * Cria uma resposta de autenticação com token Bearer
     */
    public static AuthResponse createBearerToken(String token, Long expiresIn, UserInfo userInfo) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(userInfo)
                .build();
    }
}