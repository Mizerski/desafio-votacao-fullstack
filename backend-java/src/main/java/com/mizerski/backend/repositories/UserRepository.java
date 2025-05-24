package com.mizerski.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mizerski.backend.models.entities.User;

/**
 * Interface que define os métodos para acessar os dados do usuário
 */
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Busca um usuário pelo email
     * 
     * @param email Email do usuário
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca um usuário pelo ID
     * 
     * @param id ID do usuário
     * @return Optional<User>
     */
    Optional<User> findById(String id);

}
