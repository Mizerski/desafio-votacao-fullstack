package com.mizerski.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mizerski.backend.dtos.request.CreateUserRequest;
import com.mizerski.backend.exceptions.ConflictException;
import com.mizerski.backend.exceptions.NotFoundException;
import com.mizerski.backend.dtos.response.UserListResponse;
import com.mizerski.backend.dtos.response.UserResponse;
import com.mizerski.backend.models.entities.User;
import com.mizerski.backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço para gerenciar usuários
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Cria um novo usuário
     * 
     * @param request DTO de criação de usuário
     * @return UserResponse
     */
    public UserResponse createUser(CreateUserRequest request) {

        log.info("Criando usuário: {}", request);

        // Valida se o email já está cadastrado
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email já cadastrado" + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // TODO: Encriptar a senha
        user.setDocument(request.getDocument());

        userRepository.save(user);

        log.info("Usuário criado com sucesso: {}", user);

        return UserResponse.fromEntity(user);

    }

    /**
     * Busca um usuário pelo ID
     * 
     * @param id ID do usuário
     * @return UserResponse
     */
    @Transactional(readOnly = true)
    public UserResponse findById(String id) {
        log.info("Buscando usuário pelo ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        log.info("Usuário encontrado: {}", user);

        return UserResponse.fromEntity(user);
    }

    /**
     * Busca usuário por email
     * 
     * @param email Email do usuário
     * @return UserResponse
     */
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        log.info("Buscando usuário pelo email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        log.info("Usuário encontrado: {}", user);

        return UserResponse.fromEntity(user);
    }

    /**
     * Lista todos os usuários com paginação
     * 
     * @param pageable Pageable
     * @return UserResponse
     */
    @Transactional(readOnly = true)
    public UserListResponse findAll(Pageable pageable) {

        log.debug("Listando usuários - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<User> users = userRepository.findAll(pageable);

        List<UserResponse> userResponses = users.getContent().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());

        return UserListResponse.builder()
                .users(userResponses)
                .totalItems(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .currentPage(pageable.getPageNumber())
                .hasNext(users.hasNext())
                .hasPrevious(users.hasPrevious())
                .build();

    }

    /**
     * Verifica se o usuário existe
     * 
     * @param id ID do usuário
     * @return boolean
     */
    @Transactional(readOnly = true)
    public boolean existsById(String id) {
        log.debug("Verificando se o usuário existe: {}", id);
        return userRepository.existsById(id);
    }

    /**
     * Verifica se o email já está cadastrado
     * 
     * @param email Email do usuário
     * @return boolean
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(CreateUserRequest request) {
        log.debug("Verificando se o email já está cadastrado: {}", request.getEmail());
        return userRepository.findByEmail(request.getEmail()).isPresent();
    }

    /**
     * Contabiliza o total de usuários
     * 
     * @return long
     */
    @Transactional(readOnly = true)
    public long count() {
        log.debug("Contabilizando total de usuários");
        return userRepository.count();
    }
}
