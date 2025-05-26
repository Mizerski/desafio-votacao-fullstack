package com.mizerski.backend.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para iniciar sessão de votação
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartSessionRequest {

    @NotNull(message = "A duração é obrigatória")
    @Min(value = 1, message = "A duração deve ser de pelo menos 1 minuto")
    @Max(value = 1440, message = "A duração não pode exceder 1440 minutos (24 horas)")
    private Integer durationInMinutes;
}