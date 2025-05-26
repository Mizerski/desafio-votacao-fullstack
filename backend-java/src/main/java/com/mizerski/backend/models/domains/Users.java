package com.mizerski.backend.models.domains;

import java.time.LocalDateTime;

import com.mizerski.backend.models.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade de domínio que representa um usuário
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    private String id;
    private String name;
    private String email;
    private String document;
    private String password;
    @Builder.Default
    private UserRole role = UserRole.USER;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isValidEmail() {
        return email != null && email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    public boolean isValidDocument() {
        return document != null && !document.trim().isEmpty();
    }

    public boolean isValidPassword() {
        return password != null && !password.trim().isEmpty();
    }

    public boolean isValidName() {
        return name != null && !name.trim().isEmpty();
    }

}
