package com.mizerski.backend.models.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mizerski.backend.dtos.request.CreateVoteRequest;
import com.mizerski.backend.dtos.response.VoteResponse;
import com.mizerski.backend.models.domains.Votes;
import com.mizerski.backend.models.entities.VoteEntity;

/**
 * Mapper para conversão entre VoteEntity e Votes (domínio)
 * Utiliza MapStruct para gerar implementações automáticas.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface VoteMapper {

    /**
     * Converte VoteEntity para Votes (domínio)
     */
    Votes toDomain(VoteEntity entity);

    /**
     * Converte Votes (domínio) para VoteEntity
     */
    @Mapping(target = "createdAt")
    @Mapping(target = "updatedAt")
    @InheritInverseConfiguration
    VoteEntity toEntity(Votes domain);

    /**
     * Converte VoteEntity para VoteResponse (DTO)
     */
    VoteResponse toResponse(Votes domain);

    /**
     * Converte CreateVoteRequest (DTO) para Votes (domínio)
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // TODO: Verificar se é necessário, possivelmente pode estourar erro de
                                             // integridade
    @Mapping(target = "agenda", ignore = true) // TODO: Verificar se é necessário, possivelmente pode estourar erro de
                                               // integridade
    Votes fromCreateRequest(CreateVoteRequest request);

    /**
     * Converte VoteEntity diretamente para VoteResponse (DTO)
     */
    VoteResponse toResponse(VoteEntity entity);

}