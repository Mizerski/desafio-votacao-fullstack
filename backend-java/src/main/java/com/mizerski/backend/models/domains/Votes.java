package com.mizerski.backend.models.domains;

import java.time.LocalDateTime;

import com.mizerski.backend.models.enums.VoteType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Domínio que representa um voto
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Votes {

    private String id;
    private VoteType voteType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
