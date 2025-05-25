package com.mizerski.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mizerski.backend.annotations.Idempotent;
import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.exceptions.BadRequestException;
import com.mizerski.backend.exceptions.NotFoundException;
import com.mizerski.backend.models.entities.AgendaEntity;
import com.mizerski.backend.models.enums.AgendaResult;
import com.mizerski.backend.models.enums.AgendaStatus;
import com.mizerski.backend.models.enums.VoteType;
import com.mizerski.backend.models.mappers.AgendaMapper;
import com.mizerski.backend.repositories.AgendaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço para gerenciar operações relacionadas a horários de pautas
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AgendaTimeService {

    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;

    /**
     * Inicia o timer de uma pauta com tratamento de idempotência
     * 
     * @param agendaId          ID da pauta a ser iniciada
     * @param durationInMinutes Tempo de duração da pauta em minutos
     * @return Dados da pauta iniciada
     */
    @Transactional
    @Idempotent(expireAfterSeconds = 180, includeUserId = false) // 3 minutos para início de pauta
    public AgendaResponse startAgendaTimer(String agendaId, int durationInMinutes) {
        AgendaEntity agendaEntity = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada com ID: " + agendaId));

        if (agendaEntity.getStatus() == AgendaStatus.CANCELLED || agendaEntity.getStatus() == AgendaStatus.FINISHED) {
            throw new BadRequestException("A pauta não pode ser iniciada pois está cancelada ou encerrada");
        }

        agendaEntity.setStatus(AgendaStatus.IN_PROGRESS);

        agendaRepository.save(agendaEntity);

        return agendaMapper.toResponse(agendaEntity);
    }

    /**
     * Atualiza o resultado parcial dos votos de uma pauta
     *
     * @param agendaId ID da pauta a ser atualizada
     * @param voteType Tipo de voto a ser atualizado (YES/NO)
     * @return Dados da pauta atualizada
     */
    @Transactional
    public AgendaResponse updateAgendaVotes(String agendaId, VoteType voteType) {
        AgendaEntity agendaEntity = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada com ID: " + agendaId));

        agendaEntity.setTotalVotes(agendaEntity.getTotalVotes() + 1);

        if (voteType == VoteType.YES) {
            agendaEntity.setYesVotes(agendaEntity.getYesVotes() + 1);
        } else if (voteType == VoteType.NO) {
            agendaEntity.setNoVotes(agendaEntity.getNoVotes() + 1);
        }

        agendaRepository.save(agendaEntity);

        return agendaMapper.toResponse(agendaEntity);
    }

    /**
     * Calcula e atualiza o resultado final de uma pauta de forma eficiente com
     * tratamento de idempotência.
     *
     * @param agendaId ID da pauta a ser calculada
     * @return Dados da pauta calculada
     */
    @Transactional
    @Idempotent(expireAfterSeconds = 3600, includeUserId = false) // 1 hora para finalização de pauta
    public AgendaResponse calculateAgendaResult(String agendaId) {
        AgendaEntity agendaEntity = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada com ID: " + agendaId));

        final int yes = agendaEntity.getYesVotes();
        final int no = agendaEntity.getNoVotes();
        final int total = agendaEntity.getTotalVotes();

        AgendaResult result = calculateResult(yes, no, total);

        agendaEntity.setStatus(AgendaStatus.FINISHED);
        agendaEntity.setResult(result);
        agendaEntity.setIsActive(false);

        agendaRepository.save(agendaEntity);

        return agendaMapper.toResponse(agendaEntity);
    }

    /**
     * Retorna o resultado da pauta baseado nos votos.
     *
     * @param yes   Quantidade de votos SIM
     * @param no    Quantidade de votos NÃO
     * @param total Total de votos
     * @return Resultado da pauta
     */
    private AgendaResult calculateResult(int yes, int no, int total) {
        return (total == 0) ? AgendaResult.UNVOTED
                : (yes > no) ? AgendaResult.APPROVED
                        : (no > yes) ? AgendaResult.REJECTED
                                : AgendaResult.TIE;
    }
}
