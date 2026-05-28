package com.enspy.csi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "remboursement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Remboursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double montant;

    @Column(name = "date_remboursement")
    private LocalDate dateRemboursement;

    @Column(name = "mode_paiement")
    private String modePaiement;

    @OneToOne
    @JoinColumn(name = "feuille_maladie_id")
    private FeuillemMaladie feuilleMaladie;
}
