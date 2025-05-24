package com.mizerski.backend.models.entities;

import java.util.List;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

/**
 * Entidade que representa um usuário
 */
@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(unique = true)
    private String document;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votes;

}
