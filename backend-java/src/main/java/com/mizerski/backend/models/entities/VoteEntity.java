package com.mizerski.backend.models.entities;

import com.mizerski.backend.models.enums.VoteType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidade que representa um voto
 */
@Entity
@Getter
@Setter
@Table(name = "votes")
public class VoteEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = false)
    private VoteType voteType;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_id", nullable = false)
    private AgendaEntity agenda;

    public com.mizerski.backend.models.domains.Votes toDomain() {
        return com.mizerski.backend.models.domains.Votes.builder()
                .id(this.getId())
                .voteType(this.getVoteType())
                .user(this.getUser().toDomain())
                .agenda(this.getAgenda().toDomain())
                .build();
    }

}
