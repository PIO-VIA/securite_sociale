package com.enspy.csi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "specialiste")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Specialiste extends Medecin {

    @Column(name = "domaine_specialisation")
    private String domaineSpecialisation;
}
