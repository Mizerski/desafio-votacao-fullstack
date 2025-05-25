package com.mizerski.backend.dtos.response;

import java.time.LocalDateTime;

import com.mizerski.backend.models.entities.SessionEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de sessão
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionResponse {

    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String agendaId;

    /*
     * Converte um objeto Session para um objeto SessionResponse
     */
    public static SessionResponse fromEntity(SessionEntity session) {
        return SessionResponse.builder()
                .id(session.getId())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .agendaId(session.getAgenda().getId())
                .build();
    }
}
