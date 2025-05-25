package com.mizerski.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mizerski.backend.annotations.Idempotent;
import com.mizerski.backend.dtos.request.CreateUserRequest;
import com.mizerski.backend.dtos.response.PagedResponse;
import com.mizerski.backend.dtos.response.UserResponse;
import com.mizerski.backend.models.domains.Result;
import com.mizerski.backend.models.domains.Users;
import com.mizerski.backend.models.entities.UserEntity;
import com.mizerski.backend.models.mappers.UserMapper;
import com.mizerski.backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do serviço para gerenciar operações relacionadas a usuários
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ExceptionMappingService exceptionMappingService;

    /**
     * Cria um novo usuário com tratamento de idempotência
     * 
     * @param request Dados do usuário a ser criado
     * @return Result com dados do usuário criado ou erro
     */
    @Override
    @Transactional
    @Idempotent(expireAfterSeconds = 300, includeUserId = false) // 5 minutos para criação de usuário
    public Result<UserResponse> createUser(CreateUserRequest request) {
        try {
            // Validação de email já cadastrado
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return Result.error("DUPLICATE_EMAIL", "Email já cadastrado: " + request.getEmail());
            }

            // Validação de documento já cadastrado (se fornecido)
            if (request.getDocument() != null && !request.getDocument().trim().isEmpty()) {
                if (userRepository.findByDocument(request.getDocument()).isPresent()) {
                    return Result.error("DUPLICATE_DOCUMENT", "Documento já cadastrado: " + request.getDocument());
                }
            }

            // Converte DTO para Domínio
            Users userDomain = userMapper.fromCreateRequest(request);

            // Validações de domínio
            if (!userDomain.isValidEmail()) {
                return Result.error("INVALID_USER", "Email inválido");
            }

            if (!userDomain.isValidName()) {
                return Result.error("INVALID_USER", "Nome inválido");
            }

            if (!userDomain.isValidPassword()) {
                return Result.error("INVALID_USER", "Senha inválida");
            }

            // Converte Domínio para Entity para persistir
            UserEntity userEntity = userMapper.toEntity(userDomain);

            // Salva no banco
            UserEntity savedEntity = userRepository.save(userEntity);
            UserResponse response = userMapper.toResponse(savedEntity);

            log.info("Usuário criado com sucesso: {}", savedEntity.getId());
            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao criar usuário: {}", e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Busca um usuário pelo ID
     * 
     * @param id ID do usuário
     * @return Result com dados do usuário encontrado ou erro
     */
    @Override
    @Transactional(readOnly = true)
    public Result<UserResponse> getUserById(String id) {
        try {
            UserEntity userEntity = userRepository.findById(id).orElse(null);

            if (userEntity == null) {
                return Result.error("USER_NOT_FOUND", "Usuário não encontrado com ID: " + id);
            }

            UserResponse response = userMapper.toResponse(userEntity);
            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao buscar usuário por ID {}: {}", id, e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Busca todos os usuários com paginação
     * 
     * @param pageable Configuração de paginação
     * @return Result com resposta paginada de usuários ou erro
     */
    @Override
    @Transactional(readOnly = true)
    public Result<PagedResponse<UserResponse>> getAllUsers(Pageable pageable) {
        try {
            Page<UserEntity> page = userRepository.findAll(pageable);

            List<UserResponse> content = page.getContent().stream()
                    .map(userMapper::toResponse)
                    .collect(Collectors.toList());

            PagedResponse<UserResponse> response = new PagedResponse<>(
                    content, page.getNumber(), page.getSize(), page.getTotalElements());

            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao buscar todos os usuários: {}", e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }

    /**
     * Busca usuários por email com paginação
     * 
     * @param email    Email ou parte do email para busca
     * @param pageable Configuração de paginação
     * @return Result com resposta paginada de usuários encontrados ou erro
     */
    @Override
    @Transactional(readOnly = true)
    public Result<PagedResponse<UserResponse>> searchUsersByEmail(String email, Pageable pageable) {
        try {
            Page<UserEntity> page = userRepository.findByEmailContainingIgnoreCase(email, pageable);

            List<UserResponse> content = page.getContent().stream()
                    .map(userMapper::toResponse)
                    .collect(Collectors.toList());

            PagedResponse<UserResponse> response = new PagedResponse<>(
                    content, page.getNumber(), page.getSize(), page.getTotalElements());

            return Result.success(response);

        } catch (Exception e) {
            log.error("Erro ao buscar usuários por email {}: {}", email, e.getMessage(), e);
            return exceptionMappingService.mapExceptionToResult(e);
        }
    }
}