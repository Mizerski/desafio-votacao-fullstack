package com.mizerski.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mizerski.backend.dtos.request.CreateVoteRequest;
import com.mizerski.backend.dtos.response.VoteResponse;
import com.mizerski.backend.exceptions.BadRequestException;
import com.mizerski.backend.exceptions.NotFoundException;
import com.mizerski.backend.models.domains.Votes;
import com.mizerski.backend.models.entities.AgendaEntity;
import com.mizerski.backend.models.entities.VoteEntity;
import com.mizerski.backend.models.enums.AgendaStatus;
import com.mizerski.backend.models.mappers.VoteMapper;
import com.mizerski.backend.repositories.AgendaRepository;
import com.mizerski.backend.repositories.VoteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço para gerenciar operações relacionadas a votos
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteMapper voteMapper;
    private final AgendaRepository agendaRepository;

    /**
     * Cria um novo voto
     * 
     * @param request Dados do voto a ser criado
     * @return Dados do voto criado
     */
    public VoteResponse createVote(CreateVoteRequest request) {

        // Valida se a sessão está aberta
        AgendaEntity agendaEntity = agendaRepository.findById(request.getAgendaId())
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada com ID: " + request.getAgendaId()));

        if (agendaEntity.getStatus() != AgendaStatus.OPEN) {
            throw new BadRequestException("A pauta não está aberta para votação");
        }

        // Valida se o usuário já votou na pauta
        VoteEntity voteEntity = voteRepository.findByUserIdAndAgendaId(request.getUserId(), request.getAgendaId())
                .orElseThrow(() -> new NotFoundException("Voto não encontrado para o usuário: " + request.getUserId()
                        + " e pauta: " + request.getAgendaId()));

        if (voteEntity != null) {
            throw new BadRequestException("O usuário já votou na pauta");
        }

        // Converte DTO para Domínio
        Votes voteDomain = voteMapper.fromCreateRequest(request);

        // Converte Domínio para Entity para persistir
        VoteEntity voteEntityToSave = voteMapper.toEntity(voteDomain);

        // Salva no banco
        voteRepository.save(voteEntityToSave);

        return voteMapper.toResponse(voteEntityToSave);
    }

    /**
     * Busca um voto pelo ID do usuário e da pauta
     * 
     * @param userId   ID do usuário
     * @param agendaId ID da pauta
     * @return Dados do voto encontrado
     */
    public VoteResponse getVoteByUserIdAndAgendaId(String userId, String agendaId) {
        VoteEntity voteEntity = voteRepository.findByUserIdAndAgendaId(userId, agendaId)
                .orElseThrow(() -> new NotFoundException("Voto não encontrado para o usuário: " + userId
                        + " e pauta: " + agendaId));

        return voteMapper.toResponse(voteEntity);
    }

    /**
     * Busca todos os votos por pauta
     * 
     * @param agendaId ID da pauta
     * @return Lista de votos encontrados
     */
    public List<VoteResponse> getAllVotesByAgendaId(String agendaId) {
        List<VoteEntity> voteEntities = voteRepository.findByAgendaId(agendaId);

        return voteEntities.stream()
                .map(voteMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca todos os votos por usuário
     * 
     * @param userId ID do usuário
     * @return Lista de votos encontrados
     */
    public List<VoteResponse> getAllVotesByUserId(String userId) {
        List<VoteEntity> voteEntities = voteRepository.findByUserId(userId);

        return voteEntities.stream()
                .map(voteMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca todos os votos por pauta e usuário
     * 
     * @param agendaId ID da pauta
     * @param userId   ID do usuário
     * @return Dados do voto encontrado
     */
    public VoteResponse getVoteByAgendaIdAndUserId(String agendaId, String userId) {
        VoteEntity voteEntity = voteRepository.findByUserIdAndAgendaId(userId, agendaId)
                .orElseThrow(() -> new NotFoundException("Voto não encontrado para o usuário: " + userId
                        + " e pauta: " + agendaId));

        return voteMapper.toResponse(voteEntity);
    }

}
