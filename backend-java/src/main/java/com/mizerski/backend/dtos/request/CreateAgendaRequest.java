package com.mizerski.backend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.mizerski.backend.models.enums.AgendaCategory;
import com.mizerski.backend.models.enums.AgendaStatus;
import com.mizerski.backend.models.enums.AgendaResult;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para criação de agenda
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgendaRequest {

    @NotBlank(message = "O título é obrigatório")
    private String title;

    @NotBlank(message = "A descrição é obrigatória")
    private String description;

    @NotNull(message = "A categoria é obrigatória")
    private AgendaCategory category;

    @NotNull(message = "O status é obrigatório")
    private AgendaStatus status;

    @NotNull(message = "O resultado é obrigatório")
    private AgendaResult result;

    private Integer totalVotes;
    private Integer yesVotes;
    private Integer noVotes;
    private Boolean isActive;
}
