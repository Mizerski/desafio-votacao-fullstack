package com.mizerski.backend.models.entities;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidade JPA que representa um usuário
 */
@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "document", unique = true)
    private String document;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VoteEntity> votes;

    public com.mizerski.backend.models.domains.Users toDomain() {
        return com.mizerski.backend.models.domains.Users.builder()
                .id(this.getId())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .name(this.getName())
                .email(this.getEmail())
                .document(this.getDocument())
                .password(this.getPassword())
                .votes(this.getVotes().stream().map(VoteEntity::toDomain).collect(Collectors.toList()))
                .build();
    }

}
