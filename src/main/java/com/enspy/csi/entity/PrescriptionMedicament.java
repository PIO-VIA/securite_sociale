package com.enspy.csi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prescription_medicament")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PrescriptionMedicament extends Prescription {

    private String medicament;
    private String posologie;
}
