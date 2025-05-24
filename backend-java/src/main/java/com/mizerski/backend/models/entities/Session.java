package com.mizerski.backend.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class Session extends BaseEntity {

    @NotBlank(message = "A data de início é obrigatória")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotBlank(message = "A data de término é obrigatória")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_id", nullable = false)
    private Agenda agenda;
}
