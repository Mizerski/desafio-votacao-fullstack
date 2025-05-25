package com.mizerski.backend.dtos.request;

import com.mizerski.backend.annotations.ValidUUID;
import com.mizerski.backend.models.enums.VoteType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
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
    @ValidUUID(message = "ID da agenda deve ser um UUID válido")
    private String agendaId;

    @NotBlank(message = "O ID do usuário é obrigatório")
    @ValidUUID(message = "ID do usuário deve ser um UUID válido")
    private String userId;
}
