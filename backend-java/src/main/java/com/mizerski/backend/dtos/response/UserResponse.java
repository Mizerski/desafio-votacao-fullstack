package com.mizerski.backend.dtos.response;

import java.time.LocalDateTime;

import com.mizerski.backend.models.entities.UserEntity;

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

    public static UserResponse fromEntity(UserEntity userEntity) {
        return UserResponse.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .document(userEntity.getDocument())
                .createdAt(userEntity.getCreatedAt())
                .totalVotes(userEntity.getVotes() != null ? userEntity.getVotes().size() : 0)
                .build();
    }

}
