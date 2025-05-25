package com.mizerski.backend.services;

import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.enums.VoteType;

/**
 * Interface para serviço de gerenciamento de operações relacionadas a horários
 * de pautas
 */
public interface AgendaTimeService {

    /**
     * Inicia o timer de uma pauta com tratamento de idempotência
     * 
     * @param agendaId          ID da pauta a ser iniciada
     * @param durationInMinutes Tempo de duração da pauta em minutos
     * @return Result com dados da pauta iniciada ou erro
     */
    Result<AgendaResponse> startAgendaTimer(String agendaId, int durationInMinutes);

    /**
     * Atualiza o resultado parcial dos votos de uma pauta
     *
     * @param agendaId ID da pauta a ser atualizada
     * @param voteType Tipo de voto a ser atualizado (YES/NO)
     * @return Result com dados da pauta atualizada ou erro
     */
    Result<AgendaResponse> updateAgendaVotes(String agendaId, VoteType voteType);

    /**
     * Calcula e atualiza o resultado final de uma pauta de forma eficiente com
     * tratamento de idempotência.
     *
     * @param agendaId ID da pauta a ser calculada
     * @return Result com dados da pauta calculada ou erro
     */
    Result<AgendaResponse> calculateAgendaResult(String agendaId);
}