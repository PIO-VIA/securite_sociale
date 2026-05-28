package com.enspy.csi.dto.request;

import lombok.Data;

@Data
public class PrescriptionRequestDTO {
    private Long consultationId;
    private String medicament;
    private String posologie;
    private String matriculeMedecin;
    private String motif;
}
