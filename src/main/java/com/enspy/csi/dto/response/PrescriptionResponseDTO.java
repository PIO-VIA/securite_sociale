package com.enspy.csi.dto.response;

import lombok.Data;

@Data
public class PrescriptionResponseDTO {
    private Long id;
    private Long consultationId;
    private String type;
    private String medicament;
    private String posologie;
    private String matriculeMedecin;
    private String motif;
}
