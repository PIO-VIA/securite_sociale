package com.enspy.csi.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MedecinResponseDTO {
    private Long id;
    private String nom;
    private LocalDate dateNaissance;
    private String sexe;
    private String indicatifPays;
    private String numTelephone;
    private String matricule;
    private Boolean estAssure;
    private String type;
    private String domaineSpecialisation;
    private String email;
    private String photoUrl;
    private Long medecinTraitantId;
    private String medecinTraitantNom;
    private String medecinTraitantMatricule;
}
