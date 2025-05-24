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

import com.mizerski.backend.models.enums.AgendaStatus;
import com.mizerski.backend.models.enums.AgendaCategory;
import com.mizerski.backend.models.enums.AgendaResult;

import lombok.Getter;
import lombok.Setter;

/**
 * Entidade que representa uma agenda de votação
 */
@Entity
@Getter
@Setter
@Table(name = "agendas")
public class Agenda extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

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
