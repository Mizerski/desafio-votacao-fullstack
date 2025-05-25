package com.mizerski.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mizerski.backend.dtos.request.CreateAgendaRequest;
import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.exceptions.NotFoundException;
import com.mizerski.backend.models.domains.Agendas;
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
@Transactional
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final AgendaMapper agendaMapper;

    /**
     * Cria uma nova pauta
     * 
     * @param request Dados da pauta a ser criada
     * @return Dados da pauta criada
     */
    public AgendaResponse createAgenda(CreateAgendaRequest request) {

        // Converte DTO para Domínio
        Agendas agendaDomain = agendaMapper.fromCreateRequest(request);

        // Converte Domínio para Entity para persistir
        AgendaEntity agendaEntity = agendaMapper.toEntity(agendaDomain);

        // Salva no banco
        agendaRepository.save(agendaEntity);

        return agendaMapper.toResponse(agendaEntity);
    }

    /**
     * Busca uma pauta pelo ID
     * 
     * @param id ID da pauta
     * @return Dados da pauta encontrada
     */
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
    public List<AgendaResponse> getAllAgendas() {
        List<AgendaEntity> agendaEntities = agendaRepository.findAll();

        return agendaEntities.stream()
                .map(agendaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca todas as pautas com sessões abertas
     * 
     * @return Lista de pautas com sessões abertas
     */
    public List<AgendaResponse> getAllAgendasWithOpenSessions() {
        List<AgendaEntity> agendaEntities = agendaRepository.findByStatusIn(
                List.of(AgendaStatus.OPEN, AgendaStatus.IN_PROGRESS));

        return agendaEntities.stream()
                .map(agendaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca todas as pautas encerradas
     * 
     * @return Lista de pautas encerradas
     */
    public List<AgendaResponse> getAllAgendasFinished() {
        List<AgendaEntity> agendaEntities = agendaRepository
                .findByStatusIn(List.of(AgendaStatus.FINISHED, AgendaStatus.CANCELLED));

        return agendaEntities.stream()
                .map(agendaMapper::toResponse)
                .collect(Collectors.toList());
    }
}
