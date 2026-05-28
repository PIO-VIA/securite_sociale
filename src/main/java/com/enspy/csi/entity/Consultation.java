package com.enspy.csi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "consultation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"prescriptions", "feuilleMaladie"})
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "assure_id")
    private Assure assure;

    @ManyToOne
    @JoinColumn(name = "generaliste_id")
    private Generaliste generaliste;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL)
    private List<Prescription> prescriptions;

    @OneToOne(mappedBy = "consultation", cascade = CascadeType.ALL)
    private FeuillemMaladie feuilleMaladie;
}
