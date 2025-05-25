package com.mizerski.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mizerski.backend.dtos.request.CreateUserRequest;
import com.mizerski.backend.dtos.response.UserResponse;
import com.mizerski.backend.exceptions.ConflictException;
import com.mizerski.backend.exceptions.NotFoundException;
import com.mizerski.backend.models.domains.Users;
import com.mizerski.backend.models.entities.UserEntity;
import com.mizerski.backend.models.mappers.UserMapper;
import com.mizerski.backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço para gerenciar operações relacionadas a usuários
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Cria um novo usuário
     * 
     * @param request Dados do usuário a ser criado
     * @return Dados do usuário criado
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        // Validação de email já cadastrado
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email já cadastrado: " + request.getEmail());
        }

        // Converte DTO para Domínio
        Users userDomain = userMapper.fromCreateRequest(request);

        if (!userDomain.isValidEmail()) {
            throw new IllegalArgumentException("Email inválido");
        }

        // Converte Domínio para Entity para persistir
        UserEntity userEntity = userMapper.toEntity(userDomain);

        // Salva no banco
        userRepository.save(userEntity);

        return userMapper.toResponse(userEntity); // Entidade -> Domínio -> DTO
    }

    /**
     * Busca um usuário pelo ID
     * 
     * @param id ID do usuário
     * @return Dados do usuário encontrado
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(String id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com ID: " + id));

        return userMapper.toResponse(userEntity); // Entidade -> Domínio -> DTO
    }

    /**
     * Busca todos os usuários
     * 
     * @return Lista de usuários
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();

        return userEntities.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList()); // Entidade -> DTO

    }
}