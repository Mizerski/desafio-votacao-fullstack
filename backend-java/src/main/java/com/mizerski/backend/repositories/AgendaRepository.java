package com.mizerski.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mizerski.backend.models.entities.AgendaEntity;

/**
 * Interface que define os métodos para acessar os dados da agenda
 */
public interface AgendaRepository extends JpaRepository<AgendaEntity, String> {

    /**
     * Busca uma agenda pelo título
     * 
     * @param title Título da agenda
     * @return Optional<Agenda>
     */
    Optional<AgendaEntity> findByTitle(String title);

    /**
     * Busca todas as agendas encerradas
     * 
     * @return List<Agenda>
     */
    List<AgendaEntity> findAllFinished();

    /**
     * Busca todas as agendas abertas
     * 
     * @return List<Agenda>
     */
    List<AgendaEntity> findAllOpen();

}
