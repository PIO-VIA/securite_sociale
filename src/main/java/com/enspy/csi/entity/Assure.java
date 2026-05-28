package com.enspy.csi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "assure")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"consultations"})
public class Assure extends Personne {

    @Column(name = "id_assure", unique = true)
    private String idAssure;

    @Column(name = "date_inscription")
    private LocalDate dateInscription;

    private String profession;

    @Column(name = "statut_matrimonial")
    private String statutMatrimoniale;

    @Column(name = "groupe_sanguin")
    private String groupeSanguin;

    @ManyToOne
    @JoinColumn(name = "medecin_traitant_id")
    private Generaliste medecinTraitant;

    @OneToMany(mappedBy = "assure")
    private List<Consultation> consultations;
}
