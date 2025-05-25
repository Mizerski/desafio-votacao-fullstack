package com.mizerski.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mizerski.backend.annotations.Idempotent;
import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.entities.AgendaEntity;
import com.mizerski.backend.models.enums.AgendaResult;
import com.mizerski.backend.models.enums.AgendaStatus;
import com.mizerski.backend.models.enums.VoteType;
import com.mizerski.backend.models.mappers.AgendaMapper;
import com.mizerski.backend.repositories.AgendaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do serviço para gerenciar operações relacionadas a horários de
 * pautas
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AgendaTimeServiceImpl implements AgendaTimeService {

    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;
    private final ExceptionMappingService exceptionMappingService;

    /**
     * Inicia o timer de uma pauta com tratamento de idempotência
     * 
     * @param agendaId          ID da pauta a ser iniciada
     * @param durationInMinutes Tempo de duração da pauta em minutos
     * @return Result com dados da pauta iniciada ou erro
     */
    @Override
    @Transactional
    @Idempotent(expireAfterSeconds = 180, includeUserId = false) // 3 minutos para início de pauta
    public Result<AgendaResponse> startAgendaTimer(String agendaId, int durationInMinutes) {
        try {
            AgendaEntity agendaEntity = agendaRepository.findById(agendaId).orElse(null);

            if (agendaEntity == null) {
                return Result.error("AGENDA_NOT_FOUND", "Pauta não encontrada com ID: " + agendaId);
            }

            if (agendaEntity.getStatus() == AgendaStatus.CANCELLED
                    || agendaEntity.getStatus() == AgendaStatus.FINISHED) {
                return Result.error("OPERATION_NOT_ALLOWED",
                        "A pauta não pode ser iniciada pois está cancelada ou encerrada");
            }

            agendaEntity.setStatus(AgendaStatus.IN_PROGRESS);
            AgendaEntity savedEntity = agendaRepository.save(agendaEntity);
            AgendaResponse response = agendaMapper.toResponse(savedEntity);

            log.info("Timer da pauta iniciado com sucesso: {}", agendaId);
            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao iniciar timer da pauta {}: {}", agendaId, e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Atualiza o resultado parcial dos votos de uma pauta
     *
     * @param agendaId ID da pauta a ser atualizada
     * @param voteType Tipo de voto a ser atualizado (YES/NO)
     * @return Result com dados da pauta atualizada ou erro
     */
    @Override
    @Transactional
    public Result<AgendaResponse> updateAgendaVotes(String agendaId, VoteType voteType) {
        try {
            AgendaEntity agendaEntity = agendaRepository.findById(agendaId).orElse(null);

            if (agendaEntity == null) {
                return Result.error("AGENDA_NOT_FOUND", "Pauta não encontrada com ID: " + agendaId);
            }

            agendaEntity.setTotalVotes(agendaEntity.getTotalVotes() + 1);

            if (voteType == VoteType.YES) {
                agendaEntity.setYesVotes(agendaEntity.getYesVotes() + 1);
            } else if (voteType == VoteType.NO) {
                agendaEntity.setNoVotes(agendaEntity.getNoVotes() + 1);
            }

            AgendaEntity savedEntity = agendaRepository.save(agendaEntity);
            AgendaResponse response = agendaMapper.toResponse(savedEntity);

            log.info("Votos da pauta atualizados com sucesso: {}", agendaId);
            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao atualizar votos da pauta {}: {}", agendaId, e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Calcula e atualiza o resultado final de uma pauta de forma eficiente com
     * tratamento de idempotência.
     *
     * @param agendaId ID da pauta a ser calculada
     * @return Result com dados da pauta calculada ou erro
     */
    @Override
    @Transactional
    @Idempotent(expireAfterSeconds = 3600, includeUserId = false) // 1 hora para finalização de pauta
    public Result<AgendaResponse> calculateAgendaResult(String agendaId) {
        try {
            AgendaEntity agendaEntity = agendaRepository.findById(agendaId).orElse(null);

            if (agendaEntity == null) {
                return Result.error("AGENDA_NOT_FOUND", "Pauta não encontrada com ID: " + agendaId);
            }

            final int yes = agendaEntity.getYesVotes();
            final int no = agendaEntity.getNoVotes();
            final int total = agendaEntity.getTotalVotes();

            AgendaResult result = calculateResult(yes, no, total);

            agendaEntity.setStatus(AgendaStatus.FINISHED);
            agendaEntity.setResult(result);
            agendaEntity.setIsActive(false);

            AgendaEntity savedEntity = agendaRepository.save(agendaEntity);
            AgendaResponse response = agendaMapper.toResponse(savedEntity);

            log.info("Resultado da pauta calculado com sucesso: {} - Resultado: {}", agendaId, result);
            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao calcular resultado da pauta {}: {}", agendaId, e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
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
