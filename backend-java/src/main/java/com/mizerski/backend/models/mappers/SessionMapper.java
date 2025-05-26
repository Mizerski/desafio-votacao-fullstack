package com.mizerski.backend.models.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.mizerski.backend.dtos.request.CreateSessionRequest;
import com.mizerski.backend.dtos.response.SessionResponse;
import com.mizerski.backend.models.domains.Sessions;
import com.mizerski.backend.models.entities.SessionEntity;

/**
 * Mapper para conversão entre SessionEntity e Sessions (domínio)
 * Utiliza MapStruct para gerar implementações automáticas.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SessionMapper {

    /**
     * Converte SessionEntity para Sessions (domínio)
     */
    Sessions toDomain(SessionEntity entity);

    /**
     * Converte Sessions (domínio) para SessionEntity
     */
    @Named("toEntity")
    @Mapping(target = "startTime", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SessionEntity toEntity(Sessions domain);

    /**
     * Converte Sessions (domínio) para SessionResponse (DTO)
     */
    @Mapping(target = "agendaId", ignore = true)
    SessionResponse toResponse(Sessions domain);

    /**
     * Converte CreateSessionRequest (DTO) para Sessions (domínio)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Sessions fromCreateRequest(CreateSessionRequest request);

    /**
     * Converte SessionEntity diretamente para SessionResponse (DTO)
     */
    @Mapping(target = "agendaId", source = "agenda.id")
    SessionResponse toResponse(SessionEntity entity);

    /**
     * Converte Sessions (domínio) para SessionEntity ignorando agenda
     * Para evitar dependência circular, a agenda deve ser setada manualmente
     */
    @Named("toEntityWithoutAgenda")
    @Mapping(target = "startTime", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "agenda", ignore = true)
    SessionEntity toEntityWithoutAgenda(Sessions domain);

    /**
     * Converte Sessions (domínio) para SessionResponse ignorando agenda
     * Para evitar dependência circular, o agendaId deve ser setado manualmente
     */
    @Mapping(target = "agendaId", ignore = true)
    SessionResponse toResponseWithoutAgenda(Sessions domain);
}