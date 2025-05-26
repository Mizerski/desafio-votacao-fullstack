package com.mizerski.backend.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mizerski.backend.annotations.Idempotent;
import com.mizerski.backend.dtos.request.CreateSessionRequest;
import com.mizerski.backend.dtos.response.SessionResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.entities.AgendaEntity;
import com.mizerski.backend.models.entities.SessionEntity;
import com.mizerski.backend.models.enums.AgendaStatus;
import com.mizerski.backend.models.mappers.SessionMapper;
import com.mizerski.backend.repositories.AgendaRepository;
import com.mizerski.backend.repositories.SessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do serviço para gerenciar sessões de votação
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final AgendaRepository agendaRepository;
    private final SessionMapper sessionMapper;
    private final ExceptionMappingService exceptionMappingService;

    /**
     * Inicia uma nova sessão de votação para uma agenda
     * 
     * @param agendaId          ID da agenda
     * @param durationInMinutes Duração da sessão em minutos
     * @return Result com dados da sessão criada ou erro
     */
    @Override
    @Transactional
    @Idempotent(expireAfterSeconds = 300) // 5 minutos para evitar sessões duplicadas
    public Result<SessionResponse> startSession(String agendaId, int durationInMinutes) {
        try {
            // Validações básicas
            if (durationInMinutes <= 0 || durationInMinutes > 1440) { // máximo 24 horas
                return Result.error("INVALID_DURATION", "Duração deve ser entre 1 e 1440 minutos");
            }

            // Busca a agenda
            Optional<AgendaEntity> agendaOpt = agendaRepository.findById(agendaId);
            if (agendaOpt.isEmpty()) {
                return Result.error("AGENDA_NOT_FOUND", "Agenda não encontrada com ID: " + agendaId);
            }

            AgendaEntity agenda = agendaOpt.get();

            // Verifica se agenda está em status válido para iniciar sessão
            if (agenda.getStatus() != AgendaStatus.DRAFT && agenda.getStatus() != AgendaStatus.OPEN) {
                return Result.error("INVALID_AGENDA_STATUS",
                        "Agenda deve estar em status DRAFT ou OPEN para iniciar sessão");
            }

            // Verifica se já existe sessão ativa
            LocalDateTime now = LocalDateTime.now();
            if (sessionRepository.hasActiveSession(agendaId, now)) {
                return Result.error("SESSION_ALREADY_ACTIVE", "Já existe uma sessão ativa para esta agenda");
            }

            // Calcula horários da sessão
            LocalDateTime startTime = now;
            LocalDateTime endTime = startTime.plusMinutes(durationInMinutes);

            // Cria a sessão
            SessionEntity session = new SessionEntity();
            session.setStartTime(startTime);
            session.setEndTime(endTime);
            session.setAgenda(agenda);

            // Salva a sessão
            SessionEntity savedSession = sessionRepository.save(session);

            // Atualiza status da agenda para IN_PROGRESS
            agenda.setStatus(AgendaStatus.IN_PROGRESS);
            agenda.setIsActive(true);
            agendaRepository.save(agenda);

            // Converte para response
            SessionResponse response = sessionMapper.toResponse(savedSession);

            log.info("Sessão iniciada com sucesso para agenda {}: {} minutos", agendaId, durationInMinutes);
            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao iniciar sessão para agenda {}: {}", agendaId, e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Cria uma nova sessão com horários específicos
     * 
     * @param request Dados da sessão a ser criada
     * @return Result com dados da sessão criada ou erro
     */
    @Override
    @Transactional
    public Result<SessionResponse> createSession(CreateSessionRequest request) {
        try {
            // Validações básicas
            if (request.getStartTime().isAfter(request.getEndTime())) {
                return Result.error("INVALID_TIME_RANGE", "Hora de início deve ser anterior à hora de término");
            }

            if (request.getStartTime().isBefore(LocalDateTime.now())) {
                return Result.error("INVALID_START_TIME", "Hora de início deve ser no futuro");
            }

            // Busca a agenda
            Optional<AgendaEntity> agendaOpt = agendaRepository.findById(request.getAgendaId());
            if (agendaOpt.isEmpty()) {
                return Result.error("AGENDA_NOT_FOUND", "Agenda não encontrada");
            }

            AgendaEntity agenda = agendaOpt.get();

            // Verifica se já existe sessão ativa
            if (sessionRepository.hasActiveSession(request.getAgendaId(), LocalDateTime.now())) {
                return Result.error("SESSION_ALREADY_ACTIVE", "Já existe uma sessão ativa para esta agenda");
            }

            // Cria a sessão
            SessionEntity session = new SessionEntity();
            session.setStartTime(request.getStartTime());
            session.setEndTime(request.getEndTime());
            session.setAgenda(agenda);

            // Salva a sessão
            SessionEntity savedSession = sessionRepository.save(session);

            // Atualiza status da agenda se a sessão começar agora
            if (request.getStartTime().isBefore(LocalDateTime.now().plusMinutes(1))) {
                agenda.setStatus(AgendaStatus.IN_PROGRESS);
                agenda.setIsActive(true);
                agendaRepository.save(agenda);
            }

            SessionResponse response = sessionMapper.toResponse(savedSession);

            log.info("Sessão criada com sucesso para agenda {}", request.getAgendaId());
            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao criar sessão: {}", e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Busca sessão ativa para uma agenda
     * 
     * @param agendaId ID da agenda
     * @return Result com dados da sessão ativa ou erro
     */
    @Override
    @Transactional(readOnly = true)
    public Result<SessionResponse> getActiveSession(String agendaId) {
        try {
            Optional<SessionEntity> sessionOpt = sessionRepository
                    .findActiveSessionByAgendaId(agendaId, LocalDateTime.now());

            if (sessionOpt.isEmpty()) {
                return Result.error("NO_ACTIVE_SESSION", "Nenhuma sessão ativa encontrada para esta agenda");
            }

            SessionResponse response = sessionMapper.toResponse(sessionOpt.get());
            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao buscar sessão ativa para agenda {}: {}", agendaId, e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Busca todas as sessões de uma agenda
     * 
     * @param agendaId ID da agenda
     * @return Lista de sessões da agenda
     */
    @Override
    @Transactional(readOnly = true)
    public List<SessionResponse> getSessionsByAgendaId(String agendaId) {
        List<SessionEntity> sessions = sessionRepository.findByAgendaId(agendaId);
        return sessions.stream()
                .map(sessionMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Finaliza uma sessão manualmente
     * 
     * @param sessionId ID da sessão
     * @return Result indicando sucesso ou erro
     */
    @Override
    @Transactional
    public Result<Void> finalizeSession(String sessionId) {
        try {
            Optional<SessionEntity> sessionOpt = sessionRepository.findById(sessionId);
            if (sessionOpt.isEmpty()) {
                return Result.error("SESSION_NOT_FOUND", "Sessão não encontrada");
            }

            SessionEntity session = sessionOpt.get();
            AgendaEntity agenda = session.getAgenda();

            // Finaliza a agenda
            agenda.setStatus(AgendaStatus.FINISHED);
            agenda.setIsActive(false);
            agendaRepository.save(agenda);

            log.info("Sessão {} finalizada manualmente", sessionId);
            return Result.success(null);

        } catch (Exception e) {
            log.error("Erro ao finalizar sessão {}: {}", sessionId, e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Processa sessões expiradas automaticamente
     * Chamado pelo scheduler para finalizar agendas cujas sessões expiraram
     * 
     * @return Número de sessões processadas
     */
    @Override
    @Transactional
    public int processExpiredSessions() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<SessionEntity> expiredSessions = sessionRepository.findExpiredSessions(now);

            int processedCount = 0;
            for (SessionEntity session : expiredSessions) {
                AgendaEntity agenda = session.getAgenda();

                // Só processa se a agenda ainda estiver ativa
                if (agenda.getStatus() == AgendaStatus.IN_PROGRESS) {
                    agenda.setStatus(AgendaStatus.FINISHED);
                    agenda.setIsActive(false);
                    agendaRepository.save(agenda);
                    processedCount++;

                    log.info("Agenda {} finalizada automaticamente - sessão expirada", agenda.getId());
                }
            }

            if (processedCount > 0) {
                log.info("Processadas {} sessões expiradas", processedCount);
            }

            return processedCount;

        } catch (Exception e) {
            log.error("Erro ao processar sessões expiradas: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Verifica se uma agenda tem sessão ativa
     * 
     * @param agendaId ID da agenda
     * @return true se tem sessão ativa
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveSession(String agendaId) {
        return sessionRepository.hasActiveSession(agendaId, LocalDateTime.now());
    }
}