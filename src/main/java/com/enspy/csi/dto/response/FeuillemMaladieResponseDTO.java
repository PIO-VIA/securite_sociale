package com.enspy.csi.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class FeuillemMaladieResponseDTO {
    private Long id;
    private String idFeuille;
    private Double montantSoin;
    private Boolean estRembourse;
    private Long consultationId;
    private LocalDate consultationDate;
    private Long assureId;
    private String assureNom;
    private String assureIdAssure;
    private Double montantRembourse;
    // Champs remboursement enrichis — évite tout second appel réseau depuis le frontend
    private LocalDate dateRemboursement;
    private String modePaiement;
    private String statut;
}
