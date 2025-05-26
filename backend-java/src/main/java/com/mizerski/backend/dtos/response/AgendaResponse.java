package com.mizerski.backend.dtos.response;

import java.time.LocalDateTime;
import java.util.List;

import com.mizerski.backend.models.enums.AgendaCategory;
import com.mizerski.backend.models.enums.AgendaResult;
import com.mizerski.backend.models.enums.AgendaStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de agenda
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaResponse {

    private String id;
    private String title;
    private String description;
    private AgendaStatus status;
    private AgendaCategory category;
    private AgendaResult result;
    private Integer totalVotes;
    private Integer yesVotes;
    private Integer noVotes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<VoteResponse> votes;
    private List<SessionResponse> sessions;

}
