package com.mizerski.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mizerski.backend.dtos.request.CreateVoteRequest;
import com.mizerski.backend.dtos.response.PagedResponse;
import com.mizerski.backend.dtos.response.VoteResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.domains.Votes;
import com.mizerski.backend.models.entities.AgendaEntity;
import com.mizerski.backend.models.entities.UserEntity;
import com.mizerski.backend.models.entities.VoteEntity;
import com.mizerski.backend.models.enums.AgendaStatus;
import com.mizerski.backend.models.enums.VoteType;
import com.mizerski.backend.models.mappers.VoteMapper;
import com.mizerski.backend.repositories.AgendaRepository;
import com.mizerski.backend.repositories.UserRepository;
import com.mizerski.backend.repositories.VoteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do serviço para gerenciar operações relacionadas a votos
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final VoteMapper voteMapper;
    private final AgendaRepository agendaRepository;
    private final UserRepository userRepository;
    private final ExceptionMappingService exceptionMappingService;

    /**
     * Cria um novo voto
     * 
     * @param request Dados do voto a ser criado
     * @return Result com dados do voto criado ou erro
     */
    @Override
    @Transactional
    public Result<VoteResponse> createVote(CreateVoteRequest request) {
        try {
            // Valida se a sessão está aberta
            AgendaEntity agendaEntity = agendaRepository.findById(request.getAgendaId()).orElse(null);

            if (agendaEntity == null) {
                return Result.error("AGENDA_NOT_FOUND", "Pauta não encontrada com ID: " + request.getAgendaId());
            }

            if (agendaEntity.getStatus() != AgendaStatus.OPEN && agendaEntity.getStatus() != AgendaStatus.IN_PROGRESS) {
                return Result.error("AGENDA_NOT_OPEN", "A pauta não está aberta para votação");
            }

            // Valida se o usuário já votou na pauta
            if (voteRepository.findByUserIdAndAgendaId(request.getUserId(), request.getAgendaId()).isPresent()) {
                return Result.error("USER_ALREADY_VOTED", "O usuário já votou na pauta");
            }

            // Busca o usuário
            UserEntity userEntity = userRepository.findById(request.getUserId()).orElse(null);
            if (userEntity == null) {
                return Result.error("USER_NOT_FOUND", "Usuário não encontrado com ID: " + request.getUserId());
            }

            // Converte DTO para Domínio
            Votes voteDomain = voteMapper.fromCreateRequest(request);

            // Converte Domínio para Entity para persistir
            VoteEntity voteEntityToSave = voteMapper.toEntity(voteDomain);

            // Seta as entidades relacionadas
            voteEntityToSave.setUser(userEntity);
            voteEntityToSave.setAgenda(agendaEntity);

            // Salva no banco
            VoteEntity savedEntity = voteRepository.save(voteEntityToSave);

            // Atualiza os contadores de votos na agenda diretamente
            agendaEntity.setTotalVotes(agendaEntity.getTotalVotes() + 1);
            if (request.getVoteType() == VoteType.YES) {
                agendaEntity.setYesVotes(agendaEntity.getYesVotes() + 1);
            } else if (request.getVoteType() == VoteType.NO) {
                agendaEntity.setNoVotes(agendaEntity.getNoVotes() + 1);
            }
            agendaRepository.save(agendaEntity);

            VoteResponse response = voteMapper.toResponse(savedEntity);

            log.info("Voto criado com sucesso: {} - Contadores atualizados: total={}, yes={}, no={}",
                    savedEntity.getId(), agendaEntity.getTotalVotes(), agendaEntity.getYesVotes(),
                    agendaEntity.getNoVotes());
            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao criar voto: {}", e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Busca um voto pelo ID do usuário e da pauta
     * 
     * @param userId   ID do usuário
     * @param agendaId ID da pauta
     * @return Result com dados do voto encontrado ou erro
     */
    @Override
    @Transactional(readOnly = true)
    public Result<VoteResponse> getVoteByUserIdAndAgendaId(String userId, String agendaId) {
        try {
            VoteEntity voteEntity = voteRepository.findByUserIdAndAgendaId(userId, agendaId).orElse(null);

            if (voteEntity == null) {
                return Result.error("VOTE_NOT_FOUND",
                        "Voto não encontrado para o usuário: " + userId + " e pauta: " + agendaId);
            }

            VoteResponse response = voteMapper.toResponse(voteEntity);
            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao buscar voto por usuário {} e pauta {}: {}", userId, agendaId, e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Busca todos os votos por pauta
     * 
     * @param agendaId ID da pauta
     * @return Result com lista de votos encontrados ou erro
     */
    @Override
    @Transactional(readOnly = true)
    public Result<List<VoteResponse>> getAllVotesByAgendaId(String agendaId) {
        try {
            List<VoteEntity> voteEntities = voteRepository.findByAgendaId(agendaId);

            List<VoteResponse> responses = voteEntities.stream()
                    .map(voteMapper::toResponse)
                    .collect(Collectors.toList());

            return Result.success(responses);

        } catch (Exception e) {
            log.error("Erro ao buscar votos por pauta {}: {}", agendaId, e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Busca todos os votos por pauta com paginação
     * 
     * @param agendaId ID da pauta
     * @param pageable Configuração de paginação
     * @return Result com resposta paginada de votos ou erro
     */
    @Override
    @Transactional(readOnly = true)
    public Result<PagedResponse<VoteResponse>> getAllVotesByAgendaId(String agendaId, Pageable pageable) {
        try {
            Page<VoteEntity> page = voteRepository.findByAgendaId(agendaId, pageable);

            List<VoteResponse> content = page.getContent().stream()
                    .map(voteMapper::toResponse)
                    .collect(Collectors.toList());

            PagedResponse<VoteResponse> response = new PagedResponse<>(
                    content, page.getNumber(), page.getSize(), page.getTotalElements());

            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao buscar votos paginados por pauta {}: {}", agendaId, e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Busca todos os votos por usuário
     * 
     * @param userId ID do usuário
     * @return Result com lista de votos encontrados ou erro
     */
    @Override
    @Transactional(readOnly = true)
    public Result<List<VoteResponse>> getAllVotesByUserId(String userId) {
        try {
            List<VoteEntity> voteEntities = voteRepository.findByUserId(userId);

            List<VoteResponse> responses = voteEntities.stream()
                    .map(voteMapper::toResponse)
                    .collect(Collectors.toList());

            return Result.success(responses);

        } catch (Exception e) {
            log.error("Erro ao buscar votos por usuário {}: {}", userId, e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Busca todos os votos por usuário com paginação
     * 
     * @param userId   ID do usuário
     * @param pageable Configuração de paginação
     * @return Result com resposta paginada de votos ou erro
     */
    @Override
    @Transactional(readOnly = true)
    public Result<PagedResponse<VoteResponse>> getAllVotesByUserId(String userId, Pageable pageable) {
        try {
            Page<VoteEntity> page = voteRepository.findByUserId(userId, pageable);

            List<VoteResponse> content = page.getContent().stream()
                    .map(voteMapper::toResponse)
                    .collect(Collectors.toList());

            PagedResponse<VoteResponse> response = new PagedResponse<>(
                    content, page.getNumber(), page.getSize(), page.getTotalElements());

            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao buscar votos paginados por usuário {}: {}", userId, e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Busca todos os votos por pauta e usuário
     * 
     * @param agendaId ID da pauta
     * @param userId   ID do usuário
     * @return Result com dados do voto encontrado ou erro
     */
    @Override
    @Transactional(readOnly = true)
    public Result<VoteResponse> getVoteByAgendaIdAndUserId(String agendaId, String userId) {
        try {
            VoteEntity voteEntity = voteRepository.findByUserIdAndAgendaId(userId, agendaId).orElse(null);

            if (voteEntity == null) {
                return Result.error("VOTE_NOT_FOUND",
                        "Voto não encontrado para o usuário: " + userId + " e pauta: " + agendaId);
            }

            VoteResponse response = voteMapper.toResponse(voteEntity);
            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao buscar voto por pauta {} e usuário {}: {}", agendaId, userId, e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }
}
