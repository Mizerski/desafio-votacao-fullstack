package com.mizerski.backend.dtos.request;

import com.mizerski.backend.models.enums.AgendaCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de agenda
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgendaRequest {

    @NotBlank(message = "O título é obrigatório")
    @Size(min = 3, max = 200, message = "O título deve ter entre 3 e 200 caracteres")
    private String title;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(min = 10, max = 1000, message = "A descrição deve ter entre 10 e 1000 caracteres")
    private String description;

    @NotNull(message = "A categoria é obrigatória")
    private AgendaCategory category;

    private Integer totalVotes;
    private Integer yesVotes;
    private Integer noVotes;
    private Boolean isActive;
}
