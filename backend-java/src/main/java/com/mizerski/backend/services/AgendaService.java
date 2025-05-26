package com.mizerski.backend.services;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.mizerski.backend.dtos.request.CreateAgendaRequest;
import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.dtos.response.PagedResponse;
import com.mizerski.backend.models.domains.Result;

/**
 * Interface para serviço de gerenciamento de operações relacionadas a pautas
 */
public interface AgendaService {

    /**
     * Cria uma nova pauta com tratamento de idempotência
     * 
     * @param request Dados da pauta a ser criada
     * @return Result com dados da pauta criada ou erro
     */
    Result<AgendaResponse> createAgenda(CreateAgendaRequest request);

    /**
     * Busca uma pauta pelo ID
     * 
     * @param id ID da pauta
     * @return Result com dados da pauta encontrada ou erro
     */
    Result<AgendaResponse> getAgendaById(String id);

    /**
     * Busca todas as pautas
     * 
     * @return Lista de pautas
     */
    List<AgendaResponse> getAllAgendas();

    /**
     * Busca todas as pautas com paginação
     * 
     * @param pageable Configuração de paginação
     * @return Resposta paginada de pautas
     */
    PagedResponse<AgendaResponse> getAllAgendas(Pageable pageable);

    /**
     * Busca todas as pautas com sessões abertas
     * 
     * @return Lista de pautas com sessões abertas
     */
    List<AgendaResponse> getAllAgendasWithOpenSessions();

    /**
     * Busca todas as pautas com sessões abertas com paginação
     * 
     * @param pageable Configuração de paginação
     * @return Resposta paginada de pautas abertas
     */
    PagedResponse<AgendaResponse> getAllAgendasWithOpenSessions(Pageable pageable);

    /**
     * Busca todas as pautas encerradas
     * 
     * @return Lista de pautas encerradas
     */
    List<AgendaResponse> getAllAgendasFinished();

    /**
     * Busca todas as pautas encerradas com paginação
     * 
     * @param pageable Configuração de paginação
     * @return Resposta paginada de pautas encerradas
     */
    PagedResponse<AgendaResponse> getAllAgendasFinished(Pageable pageable);

    /**
     * Inicia uma sessão de votação para uma agenda
     * 
     * @param agendaId          ID da agenda
     * @param durationInMinutes Duração da sessão em minutos
     * @return Result com dados da agenda atualizada
     */
    Result<AgendaResponse> startAgendaSession(String agendaId, int durationInMinutes);
}