package com.mizerski.backend.models.enums;

/**
 * Enum que define os papéis/roles dos usuários no sistema.
 * Usado para controle de acesso e autorização.
 */
public enum UserRole {
    ADMIN("ROLE_ADMIN", "Administrador do sistema"),
    USER("ROLE_USER", "Usuário comum"),
    MODERATOR("ROLE_MODERATOR", "Moderador de conteúdo");

    private final String authority;
    private final String description;

    UserRole(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }

    /**
     * Retorna a autoridade Spring Security para este role
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * Retorna a descrição do role
     */
    public String getDescription() {
        return description;
    }

    /**
     * Converte string para UserRole
     */
    public static UserRole fromString(String role) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.name().equalsIgnoreCase(role) ||
                    userRole.authority.equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("Role inválido: " + role);
    }
}