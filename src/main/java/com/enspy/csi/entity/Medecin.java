package com.enspy.csi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medecin")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class Medecin extends Personne {

    private String matricule;

    @Column(name = "est_assure")
    private Boolean estAssure = false;

    @ManyToOne
    @JoinColumn(name = "medecin_traitant_id")
    private Generaliste medecinTraitant;
}
