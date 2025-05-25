package com.mizerski.backend.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mizerski.backend.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Serviço customizado para carregar detalhes do usuário para Spring Security.
 * Implementa UserDetailsService para integração com o sistema de autenticação.
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carrega o usuário pelo username (email)
     * 
     * @param username Email do usuário
     * @return UserDetails do usuário encontrado
     * @throws UsernameNotFoundException Se o usuário não for encontrado
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Carregando usuário por email: {}", username);

        return userRepository.findByEmailAndIsActiveTrue(username)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado ou inativo: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado: " + username);
                });
    }
}