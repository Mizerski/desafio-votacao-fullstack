package com.mizerski.backend.models.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.mizerski.backend.dtos.request.CreateAgendaRequest;
import com.mizerski.backend.dtos.response.AgendaResponse;
import com.mizerski.backend.models.domains.Agendas;
import com.mizerski.backend.models.entities.AgendaEntity;

/**
 * Mapper para conversão entre AgendaEntity e Agendas (domínio)
 * Utiliza MapStruct para gerar implementações automáticas.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgendaMapper {

    /**
     * Converte AgendaEntity para Agendas (domínio)
     */
    Agendas toDomain(AgendaEntity entity);

    /**
     * Converte Agendas (domínio) para AgendaEntity
     */
    @Named("toEntity")
    AgendaEntity toEntity(Agendas domain);

    /**
     * Converte Agendas (domínio) para AgendaResponse (DTO)
     */
    AgendaResponse toResponse(Agendas domain);

    /**
     * Converte CreateAgendaRequest (DTO) para Agendas (domínio)
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "result", ignore = true)
    Agendas fromCreateRequest(CreateAgendaRequest request);

    /**
     * Converte AgendaEntity diretamente para AgendaResponse (DTO)
     */
    AgendaResponse toResponse(AgendaEntity entity);

    /**
     * Converte Agendas (domínio) para AgendaEntity ignorando sessions
     * Para evitar dependência circular, as sessions devem ser setadas manualmente
     */
    @Named("toEntityWithoutSessions")
    AgendaEntity toEntityWithoutSessions(Agendas domain);

    /**
     * Converte Agendas (domínio) para AgendaResponse ignorando sessions
     * Para evitar dependência circular, as sessions devem ser setadas manualmente
     */
    @Mapping(target = "sessions", ignore = true)
    @Mapping(target = "votes", ignore = true)
    AgendaResponse toResponseWithoutSessions(Agendas domain);

}