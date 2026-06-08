package com.enspy.csi.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enspy.csi.dto.response.RemboursementResponseDTO;
import com.enspy.csi.entity.Consultation;
import com.enspy.csi.entity.FeuillemMaladie;
import com.enspy.csi.entity.PrescriptionConsultation;
import com.enspy.csi.entity.Remboursement;
import com.enspy.csi.exception.ResourceNotFoundException;
import com.enspy.csi.repository.FeuillemMaladieRepository;
import com.enspy.csi.repository.PrescriptionRepository;
import com.enspy.csi.repository.RemboursementRepository;
import com.enspy.csi.service.RemboursementService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RemboursementServiceImpl implements RemboursementService {

    private final RemboursementRepository remboursementRepository;
    private final FeuillemMaladieRepository feuillemMaladieRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Override
    @Transactional
    public RemboursementResponseDTO effectuerRemboursement(Long feuilleMaladieId, String modePaiement) {

        // 1. Récupérer la feuille de maladie
        FeuillemMaladie feuille = feuillemMaladieRepository.findById(feuilleMaladieId)
                .orElseThrow(() -> new ResourceNotFoundException("Feuille de maladie introuvable"));

        // 2. Vérifier que la feuille n'est pas déjà remboursée
        if (feuille.getEstRembourse()) {
            throw new IllegalStateException("Cette feuille de maladie a déjà été remboursée");
        }

        // 3. Valider et normaliser le mode de paiement
        if (modePaiement == null || modePaiement.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mode de paiement invalide. Valeurs acceptées : VIREMENT, CASH");
        }
        modePaiement = modePaiement.toUpperCase();
        if (!modePaiement.equals("VIREMENT") && !modePaiement.equals("CASH")) {
            throw new IllegalArgumentException(
                    "Mode de paiement invalide. Valeurs acceptées : VIREMENT, CASH");
        }

        // 4. Déterminer le taux de remboursement
        double taux = 1.0; // Par défaut : généraliste = 100%

        Consultation consultation = feuille.getConsultation();
        if (consultation != null) {
            // Vérifier si une PrescriptionConsultation (chez spécialiste) est liée à cette consultation
            List<PrescriptionConsultation> prescriptionsSpecialiste =
                    prescriptionRepository.findConsultationsByConsultationId(consultation.getId());
            if (prescriptionsSpecialiste != null && !prescriptionsSpecialiste.isEmpty()) {
                taux = 0.8; // Spécialiste impliqué → 80%
            }
        }

        // 5. Calculer le montant remboursé
        double montantRembourse = feuille.getMontantSoin() * taux;

        // 6. Créer le remboursement
        Remboursement remboursement = new Remboursement();
        remboursement.setMontant(montantRembourse);
        remboursement.setDateRemboursement(LocalDate.now());
        remboursement.setModePaiement(modePaiement);
        remboursement.setFeuilleMaladie(feuille);

        // 7. Marquer la feuille comme remboursée
        feuille.setEstRembourse(true);
        feuillemMaladieRepository.save(feuille);

        // 8. Sauvegarder le remboursement
        Remboursement saved = remboursementRepository.save(remboursement);

        // 9. Retourner le DTO
        return toDTO(saved);
    }

    @Override
    public RemboursementResponseDTO getRemboursementById(Long id) {
        Remboursement remboursement = remboursementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Remboursement introuvable avec l'id : " + id));
        return toDTO(remboursement);
    }

    @Override
    public Double getTotalRemboursements() {
        Double total = remboursementRepository.sumTotalRemboursements();
        return total != null ? total : 0.0;
    }

    // --- Méthode de mapping privée ---
    private RemboursementResponseDTO toDTO(Remboursement r) {
        RemboursementResponseDTO dto = new RemboursementResponseDTO();
        dto.setId(r.getId());
        dto.setMontant(r.getMontant());
        dto.setDateRemboursement(r.getDateRemboursement());
        dto.setModePaiement(r.getModePaiement());
        dto.setFeuilleMaladieId(r.getFeuilleMaladie() != null ? r.getFeuilleMaladie().getId() : null);
        return dto;
    }
}