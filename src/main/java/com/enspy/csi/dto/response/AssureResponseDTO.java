package com.enspy.csi.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AssureResponseDTO {
    private Long id;
    private String idAssure;
    private String nom;
    private LocalDate dateNaissance;
    private String sexe;
    private String indicatifPays;
    private String numTelephone;
    private String profession;
    private String statutMatrimoniale;
    private String groupeSanguin;
    private String email;
    private LocalDate dateInscription;
    private String photoUrl;

    // Médecin traitant
    private Long medecinTraitantId;
    private String medecinTraitantNom;
    private String medecinTraitantMatricule;

    private Integer nombreConsultations;
}
