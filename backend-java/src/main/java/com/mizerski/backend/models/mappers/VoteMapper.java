package com.mizerski.backend.models.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.mizerski.backend.dtos.request.CreateVoteRequest;
import com.mizerski.backend.dtos.response.VoteResponse;
import com.mizerski.backend.models.domains.Votes;
import com.mizerski.backend.models.entities.VoteEntity;

/**
 * Mapper para conversão entre VoteEntity e Votes (domínio)
 * Utiliza MapStruct para gerar implementações automáticas.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VoteMapper {

    /**
     * Converte VoteEntity para Votes (domínio)
     */
    Votes toDomain(VoteEntity entity);

    /**
     * Converte Votes (domínio) para VoteEntity
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    VoteEntity toEntity(Votes domain);

    /**
     * Converte Votes (domínio) para VoteResponse (DTO)
     */
    @Mapping(target = "user", ignore = true)
    VoteResponse toResponse(Votes domain);

    /**
     * Converte CreateVoteRequest (DTO) para Votes (domínio)
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "agenda", ignore = true)
    Votes fromCreateRequest(CreateVoteRequest request);

    /**
     * Converte VoteEntity diretamente para VoteResponse (DTO)
     */
    @Mapping(target = "user", ignore = true)
    VoteResponse toResponse(VoteEntity entity);

}