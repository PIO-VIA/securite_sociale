package com.enspy.csi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

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
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.SET_NULL)
    private Generaliste medecinTraitant;

    @OneToMany(mappedBy = "generaliste", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Consultation> consultations;
}
