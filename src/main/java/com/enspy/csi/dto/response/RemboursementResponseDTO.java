package com.enspy.csi.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RemboursementResponseDTO {
    private Long id;
    private Double montant;
    private LocalDate dateRemboursement;
    private String modePaiement;
    private String statut;
    private Long feuilleMaladieId;
    private java.util.List<Long> feuilleMaladieIds;
    private String idFeuille;
    private Double montantSoin;
    private String assureNom;
}
