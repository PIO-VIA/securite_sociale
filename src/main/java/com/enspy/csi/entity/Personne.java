package com.enspy.csi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "personne")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Personne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    private String sexe;

    @Column(name = "indicatif_pays")
    private String indicatifPays;

    @Column(name = "num_telephone")
    private String numTelephone;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "mot_de_passe")
    private String motDePasse;
}
