package com.enspy.csi.service;

import com.enspy.csi.dto.response.RemboursementResponseDTO;

import java.util.List;

public interface RemboursementService {
    RemboursementResponseDTO initierRemboursement(Long feuilleMaladieId);
    RemboursementResponseDTO confirmerRemboursement(Long feuilleMaladieId, String modePaiement);
    RemboursementResponseDTO getRemboursementById(Long id);
    RemboursementResponseDTO getByFeuilleMaladieId(Long feuilleMaladieId);
    List<RemboursementResponseDTO> getRemboursementsEnAttente();
    Double getTotalRemboursements();
}
