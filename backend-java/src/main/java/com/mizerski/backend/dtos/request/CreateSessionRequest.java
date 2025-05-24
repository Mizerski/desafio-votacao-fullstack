package com.mizerski.backend.dtos.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO para criação de sessão
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSessionRequest {

    @NotNull(message = "A hora de início é obrigatória")
    private LocalDateTime startTime;

    @NotNull(message = "A hora de término é obrigatória")
    private LocalDateTime endTime;

    @NotBlank(message = "O ID da agenda é obrigatório")
    private String agendaId;

}
