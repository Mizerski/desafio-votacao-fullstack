package com.mizerski.backend.dtos.response;

import java.time.LocalDateTime;

import com.mizerski.backend.models.enums.VoteType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de voto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResponse {

    private String id;
    private VoteType voteType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse user;

}
