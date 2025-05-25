package com.mizerski.backend.services;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.mizerski.backend.dtos.request.CreateVoteRequest;
import com.mizerski.backend.dtos.response.PagedResponse;
import com.mizerski.backend.dtos.response.VoteResponse;
import com.mizerski.backend.models.domains.Result;

/**
 * Interface para serviço de gerenciamento de operações relacionadas a votos
 */
public interface VoteService {

    /**
     * Cria um novo voto
     * 
     * @param request Dados do voto a ser criado
     * @return Result com dados do voto criado ou erro
     */
    Result<VoteResponse> createVote(CreateVoteRequest request);

    /**
     * Busca um voto pelo ID do usuário e da pauta
     * 
     * @param userId   ID do usuário
     * @param agendaId ID da pauta
     * @return Result com dados do voto encontrado ou erro
     */
    Result<VoteResponse> getVoteByUserIdAndAgendaId(String userId, String agendaId);

    /**
     * Busca todos os votos por pauta
     * 
     * @param agendaId ID da pauta
     * @return Result com lista de votos encontrados ou erro
     */
    Result<List<VoteResponse>> getAllVotesByAgendaId(String agendaId);

    /**
     * Busca todos os votos por pauta com paginação
     * 
     * @param agendaId ID da pauta
     * @param pageable Configuração de paginação
     * @return Result com resposta paginada de votos ou erro
     */
    Result<PagedResponse<VoteResponse>> getAllVotesByAgendaId(String agendaId, Pageable pageable);

    /**
     * Busca todos os votos por usuário
     * 
     * @param userId ID do usuário
     * @return Result com lista de votos encontrados ou erro
     */
    Result<List<VoteResponse>> getAllVotesByUserId(String userId);

    /**
     * Busca todos os votos por usuário com paginação
     * 
     * @param userId   ID do usuário
     * @param pageable Configuração de paginação
     * @return Result com resposta paginada de votos ou erro
     */
    Result<PagedResponse<VoteResponse>> getAllVotesByUserId(String userId, Pageable pageable);

    /**
     * Busca todos os votos por pauta e usuário
     * 
     * @param agendaId ID da pauta
     * @param userId   ID do usuário
     * @return Result com dados do voto encontrado ou erro
     */
    Result<VoteResponse> getVoteByAgendaIdAndUserId(String agendaId, String userId);
}