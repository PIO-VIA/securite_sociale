package com.enspy.csi.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AgentRequestDTO {
    private String nom;
    private LocalDate dateNaissance;
    private String sexe;
    private String indicatifPays;
    private String numTelephone;
    private String matricule;
    private String fonction;
    private String email;
    private String motDePasse;
    private String photoUrl;
}
