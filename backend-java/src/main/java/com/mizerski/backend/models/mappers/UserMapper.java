package com.mizerski.backend.models.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mizerski.backend.dtos.request.CreateUserRequest;
import com.mizerski.backend.dtos.response.UserResponse;
import com.mizerski.backend.models.domains.Users;
import com.mizerski.backend.models.entities.UserEntity;

/**
 * Mapper para conversão entre UserEntity, Users (domínio) e DTOs
 * Utiliza MapStruct para gerar implementações automáticas.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converte UserEntity para Users (domínio)
     */
    Users toDomain(UserEntity entity);

    /**
     * Converte Users (domínio) para UserEntity
     */
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @InheritInverseConfiguration
    UserEntity toEntity(Users domain);

    /**
     * Converte Users (domínio) para UserResponse (DTO)
     */
    UserResponse toResponse(Users domain);

    /**
     * Converte CreateUserRequest (DTO) para Users (domínio)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "votes", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Users fromCreateRequest(CreateUserRequest request);

    /**
     * Converte UserEntity diretamente para UserResponse (DTO)
     * 
     * @param entity entidade do usuário
     * @return DTO de resposta do usuário
     */
    @Mapping(target = "totalVotes", ignore = true)
    UserResponse toResponse(UserEntity entity);

}
