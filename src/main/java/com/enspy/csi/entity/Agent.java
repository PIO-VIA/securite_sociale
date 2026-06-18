package com.enspy.csi.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Agent de l'organisme de sécurité sociale (rôle ORGANISME).
 * Persisté afin de pouvoir conserver durablement son profil et sa photo.
 */
@Entity
@Table(name = "agent")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Agent extends Personne {

    @Column(name = "matricule", unique = true)
    private String matricule;

    private String fonction;
}
