package com.enspy.csi.dto.request;

import lombok.Data;

@Data
public class RemboursementRequestDTO {
    private Long feuilleMaladieId;
    private String modePaiement;
}
