package com.enspy.csi.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AssureRequestDTO {
    private String nom;
    private LocalDate dateNaissance;
    private String sexe;
    private String indicatifPays;
    private String numTelephone;
    private String profession;
    private String statutMatrimoniale;
    private String groupeSanguin;
    private String email;
    private String motDePasse;
    private String photoUrl;
}
