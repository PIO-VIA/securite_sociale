package com.enspy.csi.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RemboursementResponseDTO {
    private Long id;
    private Double montant;
    private LocalDate dateRemboursement;
    private String modePaiement;
    private Long feuilleMaladieId;
}
