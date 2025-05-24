package com.mizerski.backend.dtos.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.mizerski.backend.models.entities.Agenda;
import com.mizerski.backend.models.enums.AgendaStatus;
import com.mizerski.backend.models.enums.AgendaCategory;
import com.mizerski.backend.models.enums.AgendaResult;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

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

    /*
     * Converte um objeto Agenda para um objeto AgendaResponse
     */
    public static AgendaResponse fromEntity(Agenda agenda) {
        return AgendaResponse.builder()
                .id(agenda.getId())
                .title(agenda.getTitle())
                .description(agenda.getDescription())
                .status(agenda.getStatus())
                .category(agenda.getCategory())
                .result(agenda.getResult())
                .totalVotes(agenda.getTotalVotes())
                .yesVotes(agenda.getYesVotes())
                .noVotes(agenda.getNoVotes())
                .isActive(agenda.getIsActive())
                .createdAt(agenda.getCreatedAt())
                .votes(agenda.getVotes().stream().map(VoteResponse::fromEntity).collect(Collectors.toList()))
                .build();
    }
}
