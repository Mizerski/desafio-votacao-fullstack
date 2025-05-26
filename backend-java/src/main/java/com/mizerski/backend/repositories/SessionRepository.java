package com.mizerski.backend.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mizerski.backend.models.entities.SessionEntity;

/**
 * Repositório para operações de sessões de votação
 */
@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, String> {

    /**
     * Busca sessões ativas (que ainda não terminaram)
     * 
     * @param now Data/hora atual
     * @return Lista de sessões ativas
     */
    @Query("SELECT s FROM SessionEntity s WHERE s.endTime > :now")
    List<SessionEntity> findActiveSessions(@Param("now") LocalDateTime now);

    /**
     * Busca sessões que já terminaram mas ainda não foram processadas
     * 
     * @param now Data/hora atual
     * @return Lista de sessões expiradas
     */
    @Query("SELECT s FROM SessionEntity s WHERE s.endTime <= :now")
    List<SessionEntity> findExpiredSessions(@Param("now") LocalDateTime now);

    /**
     * Busca sessão ativa para uma agenda específica
     * 
     * @param agendaId ID da agenda
     * @param now      Data/hora atual
     * @return Sessão ativa se existir
     */
    @Query("SELECT s FROM SessionEntity s WHERE s.agenda.id = :agendaId AND s.endTime > :now")
    Optional<SessionEntity> findActiveSessionByAgendaId(@Param("agendaId") String agendaId,
            @Param("now") LocalDateTime now);

    /**
     * Busca todas as sessões de uma agenda
     * 
     * @param agendaId ID da agenda
     * @return Lista de sessões da agenda
     */
    @Query("SELECT s FROM SessionEntity s WHERE s.agenda.id = :agendaId ORDER BY s.startTime DESC")
    List<SessionEntity> findByAgendaId(@Param("agendaId") String agendaId);

    /**
     * Verifica se existe sessão ativa para uma agenda
     * 
     * @param agendaId ID da agenda
     * @param now      Data/hora atual
     * @return true se existe sessão ativa
     */
    @Query("SELECT COUNT(s) > 0 FROM SessionEntity s WHERE s.agenda.id = :agendaId AND s.endTime > :now")
    boolean hasActiveSession(@Param("agendaId") String agendaId, @Param("now") LocalDateTime now);
}