package com.enspy.csi.dto.request;

import lombok.Data;

@Data
public class FeuillemMaladieRequestDTO {
    private String idFeuille;
    private Double montantSoin;
    private Long consultationId;
}
