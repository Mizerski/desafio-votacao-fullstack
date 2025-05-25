package com.mizerski.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mizerski.backend.annotations.Idempotent;
import com.mizerski.backend.dtos.request.CreateAgendaRequest;
import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.dtos.response.PagedResponse;
import com.mizerski.backend.exceptions.NotFoundException;
import com.mizerski.backend.models.domains.Agendas;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.entities.AgendaEntity;
import com.mizerski.backend.models.enums.AgendaStatus;
import com.mizerski.backend.models.mappers.AgendaMapper;
import com.mizerski.backend.repositories.AgendaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço para gerenciar operações relacionadas a pautas
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;
    private final IdempotencyService idempotencyService;

    /**
     * Cria uma nova pauta com tratamento de idempotência
     * 
     * @param request Dados da pauta a ser criada
     * @return Result com dados da pauta criada ou erro
     */
    @Transactional
    @Idempotent(expireAfterSeconds = 600) // 10 minutos para criação de pauta
    public Result<AgendaResponse> createAgenda(CreateAgendaRequest request) {

        // Gera chave de idempotência baseada no título e descrição
        String idempotencyKey = idempotencyService.generateKey(
                "createAgenda",
                request.getTitle(),
                request.getDescription());

        // Verifica se operação já foi executada
        Result<AgendaResponse> cachedResult = idempotencyService.checkIdempotency(idempotencyKey);
        if (cachedResult.isSuccess()) {
            log.info("Pauta já criada anteriormente, retornando resultado do cache");
            return cachedResult;
        }

        try {
            // Verifica se já existe pauta com mesmo título (evita duplicatas)
            if (agendaRepository.existsByTitle(request.getTitle())) {
                return Result.error("DUPLICATE_TITLE", "Já existe uma pauta com este título");
            }

            // Converte DTO para Domínio
            Agendas agendaDomain = agendaMapper.fromCreateRequest(request);

            // Converte Domínio para Entity para persistir
            AgendaEntity agendaEntity = agendaMapper.toEntity(agendaDomain);

            // Define valores padrão
            agendaEntity.setStatus(AgendaStatus.DRAFT);
            agendaEntity.setTotalVotes(0);
            agendaEntity.setYesVotes(0);
            agendaEntity.setNoVotes(0);
            agendaEntity.setIsActive(false);

            // Salva no banco
            AgendaEntity savedEntity = agendaRepository.save(agendaEntity);
            AgendaResponse response = agendaMapper.toResponse(savedEntity);

            // Armazena resultado no cache de idempotência
            Result<AgendaResponse> result = Result.success(response);
            idempotencyService.storeResult(idempotencyKey, response, 600);

            log.info("Pauta criada com sucesso: {}", savedEntity.getId());
            return result;

        } catch (Exception e) {
            log.error("Erro ao criar pauta: {}", e.getMessage(), e);
            return Result.error("CREATION_ERROR", "Erro interno ao criar pauta");
        }
    }

    /**
     * Busca uma pauta pelo ID
     * 
     * @param id ID da pauta
     * @return Dados da pauta encontrada
     */
    @Transactional(readOnly = true)
    public AgendaResponse getAgendaById(String id) {
        AgendaEntity agendaEntity = agendaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada com ID: " + id));

        return agendaMapper.toResponse(agendaEntity);
    }

    /**
     * Busca todas as pautas
     * 
     * @return Lista de pautas
     */
    @Transactional(readOnly = true)
    public List<AgendaResponse> getAllAgendas() {
        List<AgendaEntity> agendaEntities = agendaRepository.findAll();

        return agendaEntities.stream()
                .map(agendaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca todas as pautas com paginação
     * 
     * @param pageable Configuração de paginação
     * @return Resposta paginada de pautas
     */
    @Transactional(readOnly = true)
    public PagedResponse<AgendaResponse> getAllAgendas(Pageable pageable) {
        Page<AgendaEntity> page = agendaRepository.findAll(pageable);

        List<AgendaResponse> content = page.getContent().stream()
                .map(agendaMapper::toResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    /**
     * Busca todas as pautas com sessões abertas
     * 
     * @return Lista de pautas com sessões abertas
     */
    @Transactional(readOnly = true)
    public List<AgendaResponse> getAllAgendasWithOpenSessions() {
        List<AgendaEntity> agendaEntities = agendaRepository.findByStatusIn(
                List.of(AgendaStatus.OPEN, AgendaStatus.IN_PROGRESS));

        return agendaEntities.stream()
                .map(agendaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca todas as pautas com sessões abertas com paginação
     * 
     * @param pageable Configuração de paginação
     * @return Resposta paginada de pautas abertas
     */
    @Transactional(readOnly = true)
    public PagedResponse<AgendaResponse> getAllAgendasWithOpenSessions(Pageable pageable) {
        Page<AgendaEntity> page = agendaRepository.findByStatusIn(
                List.of(AgendaStatus.OPEN, AgendaStatus.IN_PROGRESS), pageable);

        List<AgendaResponse> content = page.getContent().stream()
                .map(agendaMapper::toResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    /**
     * Busca todas as pautas encerradas
     * 
     * @return Lista de pautas encerradas
     */
    @Transactional(readOnly = true)
    public List<AgendaResponse> getAllAgendasFinished() {
        List<AgendaEntity> agendaEntities = agendaRepository
                .findByStatusIn(List.of(AgendaStatus.FINISHED, AgendaStatus.CANCELLED));

        return agendaEntities.stream()
                .map(agendaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca todas as pautas encerradas com paginação
     * 
     * @param pageable Configuração de paginação
     * @return Resposta paginada de pautas encerradas
     */
    @Transactional(readOnly = true)
    public PagedResponse<AgendaResponse> getAllAgendasFinished(Pageable pageable) {
        Page<AgendaEntity> page = agendaRepository.findByStatusIn(
                List.of(AgendaStatus.FINISHED, AgendaStatus.CANCELLED), pageable);

        List<AgendaResponse> content = page.getContent().stream()
                .map(agendaMapper::toResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements());
    }
}
