package com.mizerski.backend.models.entities;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter pelo menos 2 caracteres")
    @Column(name = "name", nullable = false)
    private String name;

    @Email(message = "O email deve ser válido")
    @NotBlank(message = "O email é obrigatório")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, max = 100, message = "A senha deve ter pelo menos 8 caracteres")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(unique = true)
    private String document;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votes;

}
