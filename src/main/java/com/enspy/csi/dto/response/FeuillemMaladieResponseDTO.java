package com.enspy.csi.dto.response;

import lombok.Data;

@Data
public class FeuillemMaladieResponseDTO {
    private Long id;
    private String idFeuille;
    private Double montantSoin;
    private Boolean estRembourse;
    private Long consultationId;
}
