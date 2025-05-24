package com.mizerski.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mizerski.backend.models.entities.Agenda;

/**
 * Interface que define os métodos para acessar os dados da agenda
 */
public interface AgendaRepository extends JpaRepository<Agenda, String> {

    /**
     * Busca uma agenda pelo título
     * 
     * @param title Título da agenda
     * @return Optional<Agenda>
     */
    Optional<Agenda> findByTitle(String title);

    /**
     * Busca todas as agendas encerradas
     * 
     * @return List<Agenda>
     */
    List<Agenda> findAllFinished();

    /**
     * Busca todas as agendas abertas
     * 
     * @return List<Agenda>
     */
    List<Agenda> findAllOpen();

}
