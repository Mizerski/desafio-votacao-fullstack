package com.mizerski.backend.dtos.response;

import java.time.LocalDateTime;

import com.mizerski.backend.models.entities.User;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para resposta de usuário
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String id;
    private String name;
    private String email;
    private String document;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer totalVotes;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .document(user.getDocument())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .totalVotes(user.getVotes() != null ? user.getVotes().size() : 0)
                .build();
    }

}
