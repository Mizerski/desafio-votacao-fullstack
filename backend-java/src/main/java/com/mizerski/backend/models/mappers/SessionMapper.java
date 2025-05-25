package com.mizerski.backend.models.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mizerski.backend.dtos.request.CreateSessionRequest;
import com.mizerski.backend.dtos.response.SessionResponse;
import com.mizerski.backend.models.domains.Sessions;
import com.mizerski.backend.models.entities.SessionEntity;

/**
 * Mapper para conversão entre SessionEntity e Sessions (domínio)
 * Utiliza MapStruct para gerar implementações automáticas.
 */
@Mapper(componentModel = "spring")
public interface SessionMapper {

    /**
     * Converte SessionEntity para Sessions (domínio)
     */
    Sessions toDomain(SessionEntity entity);

    /**
     * Converte Sessions (domínio) para SessionEntity
     */
    @Mapping(target = "startTime")
    @Mapping(target = "endTime")
    @Mapping(target = "createdAt")
    SessionEntity toEntity(Sessions domain);

    /**
     * Converte Sessions (domínio) para SessionResponse (DTO)
     */
    @Mapping(target = "agendaId", source = "agenda.id")
    SessionResponse toResponse(Sessions domain);

    /**
     * Converte CreateSessionRequest (DTO) para Sessions (domínio)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "agenda", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Sessions fromCreateRequest(CreateSessionRequest request);
}