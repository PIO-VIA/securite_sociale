package com.enspy.csi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "generaliste")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"consultations"})
public class Generaliste extends Medecin {

    @OneToMany(mappedBy = "generaliste")
    private List<Consultation> consultations;
}
