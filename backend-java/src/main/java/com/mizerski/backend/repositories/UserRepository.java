package com.mizerski.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mizerski.backend.models.entities.UserEntity;

/**
 * Interface que define os métodos para acessar os dados do usuário
 */
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /**
     * Busca um usuário pelo email
     * @param email Email do usuário
     * @return Optional<User>
     */
    Optional<UserEntity> findByEmail(String email);

}
