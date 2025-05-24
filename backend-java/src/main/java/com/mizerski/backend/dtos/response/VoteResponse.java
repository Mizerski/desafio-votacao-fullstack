package com.mizerski.backend.dtos.response;

import java.time.LocalDateTime;

import com.mizerski.backend.models.entities.Vote;
import com.mizerski.backend.models.enums.VoteType;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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

    /*
     * Converte um objeto Vote para um objeto VoteResponse
     */
    public static VoteResponse fromEntity(Vote vote) {
        return VoteResponse.builder()
                .id(vote.getId())
                .voteType(vote.getVoteType())
                .createdAt(vote.getCreatedAt())
                .user(UserResponse.fromEntity(vote.getUser()))
                .build();
    }

}
