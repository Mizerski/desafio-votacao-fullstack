package com.mizerski.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mizerski.backend.models.entities.VoteEntity;

/**
 * Interface que define os métodos para acessar os dados do voto
 */
public interface VoteRepository extends JpaRepository<VoteEntity, String> {

    /**
     * Busca um voto pelo ID do usuário e da agenda
     * 
     * @param userId   ID do usuário
     * @param agendaId ID da agenda
     * @return Optional<Vote>
     */
    Optional<VoteEntity> findByUserIdAndAgendaId(String userId, String agendaId);

    /**
     * Conta o número de votos por agenda
     * 
     * @param agendaId ID da agenda
     * @return Long
     */
    Optional<Long> countByAgendaId(String agendaId);

    /**
     * Busca todos os votos por agenda
     * 
     * @param agendaId ID da agenda
     * @return List<Vote>
     */
    List<VoteEntity> findByAgendaId(String agendaId);

}
