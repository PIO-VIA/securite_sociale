package com.enspy.csi.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AgentResponseDTO {
    private Long id;
    private String nom;
    private LocalDate dateNaissance;
    private String sexe;
    private String indicatifPays;
    private String numTelephone;
    private String matricule;
    private String fonction;
    private String email;
    private String photoUrl;
    private String role = "ORGANISME";
}
