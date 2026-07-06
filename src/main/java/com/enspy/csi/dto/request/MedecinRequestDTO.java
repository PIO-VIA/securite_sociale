package com.enspy.csi.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MedecinRequestDTO {
    private String nom;
    private LocalDate dateNaissance;
    private String sexe;
    private String indicatifPays;
    private String numTelephone;
    private String matricule;
    private Boolean estAssure;
    private String domaineSpecialisation;
    private String type;
    private String email;
    private String motDePasse;
    private String photoUrl;
    private Long medecinTraitantId;
}
