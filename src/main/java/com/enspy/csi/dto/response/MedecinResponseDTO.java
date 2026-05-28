package com.enspy.csi.dto.response;

import lombok.Data;

@Data
public class MedecinResponseDTO {
    private Long id;
    private String nom;
    private String matricule;
    private Boolean estAssure;
    private String type;
    private String domaineSpecialisation;
}
