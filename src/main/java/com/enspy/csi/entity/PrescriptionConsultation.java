package com.enspy.csi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prescription_consultation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PrescriptionConsultation extends Prescription {

    @Column(name = "matricule_medecin")
    private String matriculeMedecin;

    private String motif;
}
