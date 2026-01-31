package com.acervo.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

// Classe Regional (apenas preparando, talvez precise alterar)
// Algumas regras de negócio:
// - O nome não pode ser vazio
// - O regional é obrigatório

@Entity
@Table(name = "regionals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Regional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true)
    private Long externalId;

    @NotBlank(message = "Não pode ser vazio")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "Não pode ser nulo!")
    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;
}
