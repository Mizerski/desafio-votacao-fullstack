package com.mizerski.backend.dtos.request;

import lombok.Data;

import com.mizerski.backend.models.enums.VoteType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * DTO para criação de voto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVoteRequest {

    @NotNull(message = "O tipo de voto é obrigatório e deve ser 'YES' ou 'NO'")
    private VoteType voteType;

    @NotBlank(message = "O ID da agenda é obrigatório")
    private String agendaId;

    @NotBlank(message = "O ID do usuário é obrigatório")
    private String userId;
}
