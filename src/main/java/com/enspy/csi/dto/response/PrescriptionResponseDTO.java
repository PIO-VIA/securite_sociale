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

    private java.time.LocalDate dateConsultation;
    private String medecinPrescripteurNom;
    private AssureMinResponseDTO assure;

    @Data
    public static class AssureMinResponseDTO {
        private Long id;
        private String idAssure;
        private String nom;
        private String numTelephone;
        private String sexe;
        private String groupeSanguin;
    }
}
