package com.enspy.csi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "remboursement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"feuillesMaladie"})
@EqualsAndHashCode(exclude = {"feuillesMaladie"})
public class Remboursement {

    public static final String STATUT_EN_ATTENTE = "EN_ATTENTE";
    public static final String STATUT_EFFECTUE = "EFFECTUE";
    public static final String STATUT_ANNULE = "ANNULE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double montant;

    @Column(name = "date_remboursement")
    private LocalDate dateRemboursement;

    @Column(name = "mode_paiement")
    private String modePaiement;

    @Column(name = "statut")
    private String statut = STATUT_EN_ATTENTE;

    @OneToMany(mappedBy = "remboursement", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<FeuillemMaladie> feuillesMaladie = new ArrayList<>();
}
