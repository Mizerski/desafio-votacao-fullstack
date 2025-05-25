package com.mizerski.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mizerski.backend.models.entities.UserEntity;

/**
 * Interface que define os métodos para acessar os dados do usuário
 */
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /**
     * Busca um usuário pelo email
     * 
     * @param email Email do usuário
     * @return Optional<User>
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Busca usuários que contenham o email especificado
     * 
     * @param email Email ou parte do email
     * @return Lista de usuários
     */
    List<UserEntity> findByEmailContainingIgnoreCase(String email);

    /**
     * Busca usuários que contenham o email especificado com paginação
     * 
     * @param email    Email ou parte do email
     * @param pageable Configuração de paginação
     * @return Page de usuários
     */
    Page<UserEntity> findByEmailContainingIgnoreCase(String email, Pageable pageable);

}
