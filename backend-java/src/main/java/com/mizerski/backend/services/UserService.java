package com.mizerski.backend.services;

import org.springframework.data.domain.Pageable;

import com.mizerski.backend.dtos.request.CreateUserRequest;
import com.mizerski.backend.dtos.response.PagedResponse;
import com.mizerski.backend.dtos.response.UserResponse;
import com.mizerski.backend.models.domains.Result;

/**
 * Interface para serviço de gerenciamento de operações relacionadas a usuários
 */
public interface UserService {

    /**
     * Cria um novo usuário com tratamento de idempotência
     * 
     * @param request Dados do usuário a ser criado
     * @return Result com dados do usuário criado ou erro
     */
    Result<UserResponse> createUser(CreateUserRequest request);

    /**
     * Busca um usuário pelo ID
     * 
     * @param id ID do usuário
     * @return Result com dados do usuário encontrado ou erro
     */
    Result<UserResponse> getUserById(String id);

    /**
     * Busca todos os usuários com paginação
     * 
     * @param pageable Configuração de paginação
     * @return Result com resposta paginada de usuários ou erro
     */
    Result<PagedResponse<UserResponse>> getAllUsers(Pageable pageable);

    /**
     * Busca usuários por email com paginação
     * 
     * @param email    Email ou parte do email para busca
     * @param pageable Configuração de paginação
     * @return Result com resposta paginada de usuários encontrados ou erro
     */
    Result<PagedResponse<UserResponse>> searchUsersByEmail(String email, Pageable pageable);
}