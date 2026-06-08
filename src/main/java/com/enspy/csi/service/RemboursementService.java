package com.enspy.csi.service;

import com.enspy.csi.dto.response.RemboursementResponseDTO;

public interface RemboursementService {

    RemboursementResponseDTO effectuerRemboursement(Long feuilleMaladieId, String modePaiement);

    RemboursementResponseDTO getRemboursementById(Long id);

    /**
     * Méthode bonus (Tâche 3.4) : retourne le montant total de tous les remboursements.
     */
    Double getTotalRemboursements();
}