package com.mizerski.backend.services;

import java.util.List;

import com.mizerski.backend.dtos.request.CreateSessionRequest;
import com.mizerski.backend.dtos.response.SessionResponse;
import com.mizerski.backend.models.domains.Result;

/**
 * Interface para serviço de gerenciamento de sessões de votação
 */
public interface SessionService {

    /**
     * Inicia uma nova sessão de votação para uma agenda
     * 
     * @param agendaId          ID da agenda
     * @param durationInMinutes Duração da sessão em minutos
     * @return Result com dados da sessão criada ou erro
     */
    Result<SessionResponse> startSession(String agendaId, int durationInMinutes);

    /**
     * Cria uma nova sessão com horários específicos
     * 
     * @param request Dados da sessão a ser criada
     * @return Result com dados da sessão criada ou erro
     */
    Result<SessionResponse> createSession(CreateSessionRequest request);

    /**
     * Busca sessão ativa para uma agenda
     * 
     * @param agendaId ID da agenda
     * @return Result com dados da sessão ativa ou erro
     */
    Result<SessionResponse> getActiveSession(String agendaId);

    /**
     * Busca todas as sessões de uma agenda
     * 
     * @param agendaId ID da agenda
     * @return Lista de sessões da agenda
     */
    List<SessionResponse> getSessionsByAgendaId(String agendaId);

    /**
     * Finaliza uma sessão manualmente
     * 
     * @param sessionId ID da sessão
     * @return Result indicando sucesso ou erro
     */
    Result<Void> finalizeSession(String sessionId);

    /**
     * Processa sessões expiradas automaticamente
     * Chamado pelo scheduler para finalizar agendas cujas sessões expiraram
     * 
     * @return Número de sessões processadas
     */
    int processExpiredSessions();

    /**
     * Verifica se uma agenda tem sessão ativa
     * 
     * @param agendaId ID da agenda
     * @return true se tem sessão ativa
     */
    boolean hasActiveSession(String agendaId);
}