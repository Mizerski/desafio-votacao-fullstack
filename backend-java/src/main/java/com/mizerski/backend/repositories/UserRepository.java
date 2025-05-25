package com.mizerski.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mizerski.backend.models.entities.UserEntity;

/**
 * Repositório para operações de usuário no banco de dados.
 * Inclui métodos específicos para autenticação e autorização.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /**
     * Busca usuário por email (usado para login)
     * 
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Busca usuário por email e que esteja ativo
     * 
     * @param email Email do usuário
     * @return Optional contendo o usuário ativo se encontrado
     */
    Optional<UserEntity> findByEmailAndIsActiveTrue(String email);

    /**
     * Verifica se existe usuário com o email informado
     * 
     * @param email Email a ser verificado
     * @return true se existe usuário com o email
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se existe usuário com o documento informado
     * 
     * @param document Documento a ser verificado
     * @return true se existe usuário com o documento
     */
    boolean existsByDocument(String document);

    /**
     * Busca usuário por documento
     * 
     * @param document Documento do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<UserEntity> findByDocument(String document);

    /**
     * Atualiza o último login do usuário
     * 
     * @param userId ID do usuário
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.lastLogin = CURRENT_TIMESTAMP, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") String userId);

    /**
     * Ativa ou desativa um usuário
     * 
     * @param userId   ID do usuário
     * @param isActive Status ativo/inativo
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.isActive = :isActive, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void updateUserActiveStatus(@Param("userId") String userId, @Param("isActive") Boolean isActive);

    /**
     * Marca email como verificado
     * 
     * @param userId ID do usuário
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.isEmailVerified = true, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    void markEmailAsVerified(@Param("userId") String userId);

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
