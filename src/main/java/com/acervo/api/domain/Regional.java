package com.acervo.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

// Classe Regional (apenas preparando, talvez precise alterar)
// Algumas regras de negócio:
// - O nome não pode ser vazio
// - O regional é obrigatório
// - O regional pode ter vários artistas
// - O regional pode ter vários álbuns

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

    @NotBlank(message = "Não pode ser vazio")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "Não pode ser nulo!")
    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;
}
