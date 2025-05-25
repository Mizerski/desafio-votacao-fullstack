package com.mizerski.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mizerski.backend.models.entities.AgendaEntity;
import com.mizerski.backend.models.enums.AgendaStatus;

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
     * Verifica se existe uma agenda com o título especificado
     * 
     * @param title Título da agenda
     * @return true se existir, false caso contrário
     */
    boolean existsByTitle(String title);

    /**
     * Busca todas as agendas abertas
     * 
     * @return List<Agenda>
     */
    List<AgendaEntity> findByStatusIn(List<AgendaStatus> statuses);

    /**
     * Busca todas as agendas abertas com paginação
     * 
     * @param statuses Lista de status
     * @param pageable Configuração de paginação
     * @return Page<AgendaEntity>
     */
    Page<AgendaEntity> findByStatusIn(List<AgendaStatus> statuses, Pageable pageable);

}
