package com.mizerski.backend.services;

import org.springframework.stereotype.Service;

import com.mizerski.backend.dtos.request.CreateUserRequest;
import com.mizerski.backend.dtos.response.UserResponse;
import com.mizerski.backend.models.entities.User;
import com.mizerski.backend.repositories.UserRepository;

import jakarta.transaction.Transactional;
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
            throw new RuntimeException("Email já cadastrado" + request.getEmail());
        }

        // Cria o usuário

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // TODO: Encriptar a senha
        user.setDocument(request.getDocument());

        userRepository.save(user);

        log.info("Usuário criado com sucesso: {}", user);

        return UserResponse.fromEntity(user);

    }
}
