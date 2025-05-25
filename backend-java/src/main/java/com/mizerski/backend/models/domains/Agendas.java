package com.mizerski.backend.models.domains;

import java.time.LocalDateTime;
import java.util.List;

import com.mizerski.backend.models.enums.AgendaCategory;
import com.mizerski.backend.models.enums.AgendaResult;
import com.mizerski.backend.models.enums.AgendaStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Domínio que representa uma agenda
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Agendas {

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
    private List<Votes> votes;
    private List<Sessions> sessions;
}
