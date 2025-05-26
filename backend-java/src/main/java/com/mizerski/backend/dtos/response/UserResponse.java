package com.mizerski.backend.dtos.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

}
