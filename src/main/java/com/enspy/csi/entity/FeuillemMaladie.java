package com.enspy.csi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feuille_maladie")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"remboursement"})
@EqualsAndHashCode(exclude = {"remboursement"})
public class FeuillemMaladie {

    public static final String STATUT_ACTIF = "ACTIF";
    public static final String STATUT_ANNULE = "ANNULE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_feuille", unique = true)
    private String idFeuille;

    @Column(name = "montant_soin")
    private Double montantSoin;

    @Column(name = "est_rembourse")
    private Boolean estRembourse = false;

    @Column(name = "statut")
    private String statut = STATUT_ACTIF;

    @OneToOne
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;

    @ManyToOne
    @JoinColumn(name = "remboursement_id")
    private Remboursement remboursement;
}
