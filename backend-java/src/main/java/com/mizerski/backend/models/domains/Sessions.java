package com.mizerski.backend.models.domains;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Domínio que representa uma sessão
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Sessions {

    private String id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Agendas agenda;
}
