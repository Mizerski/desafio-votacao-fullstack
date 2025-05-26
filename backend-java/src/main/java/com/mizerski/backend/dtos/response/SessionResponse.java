package com.mizerski.backend.dtos.response;

import java.time.LocalDateTime;

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

}
