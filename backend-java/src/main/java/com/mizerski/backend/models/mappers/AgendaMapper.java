package com.mizerski.backend.models.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.mizerski.backend.dtos.request.CreateAgendaRequest;
import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.models.domains.Agendas;
import com.mizerski.backend.models.entities.AgendaEntity;

/**
 * Mapper para conversão entre AgendaEntity e Agendas (domínio)
 * Utiliza MapStruct para gerar implementações automáticas.
 */
@Mapper(componentModel = "spring", uses = { VoteMapper.class,
        UserMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgendaMapper {

    /**
     * Converte AgendaEntity para Agendas (domínio)
     */
    Agendas toDomain(AgendaEntity entity);

    /**
     * Converte Agendas (domínio) para AgendaEntity
     */
    AgendaEntity toEntity(Agendas domain);

    /**
     * Converte Agendas (domínio) para AgendaResponse (DTO)
     */
    @Mapping(target = "votes", ignore = true)
    AgendaResponse toResponse(Agendas domain);

    /**
     * Converte CreateAgendaRequest (DTO) para Agendas (domínio)
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sessions", ignore = true)
    @Mapping(target = "votes", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "result", ignore = true)
    Agendas fromCreateRequest(CreateAgendaRequest request);

    /**
     * Converte AgendaEntity diretamente para AgendaResponse (DTO)
     */
    @Mapping(target = "votes", ignore = true)
    AgendaResponse toResponse(AgendaEntity entity);

}