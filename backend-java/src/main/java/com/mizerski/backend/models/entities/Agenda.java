package com.mizerski.backend.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.mizerski.backend.models.enums.AgendaStatus;
import com.mizerski.backend.models.enums.AgendaCategory;
import com.mizerski.backend.models.enums.AgendaResult;

@Entity
@Table(name = "agendas")
public class Agenda extends BaseEntity {

    @Size(min = 10, max = 100, message = "O título deve ter pelo menos 10 caracteres")
    @NotBlank(message = "O título é obrigatório")
    @Column(name = "title", nullable = false)
    private String title;

    @Size(min = 20, max = 500, message = "A descrição deve ter pelo menos 20 caracteres")
    @NotBlank(message = "A descrição é obrigatória")
    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AgendaStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private AgendaCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false)
    private AgendaResult result;

    @Column(name = "total_votes", nullable = false)
    private Integer totalVotes;

    @Column(name = "yes_votes", nullable = false)
    private Integer yesVotes;

    @Column(name = "no_votes", nullable = false)
    private Integer noVotes;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votes;

    @OneToMany(mappedBy = "agenda", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Session> sessions;

}
