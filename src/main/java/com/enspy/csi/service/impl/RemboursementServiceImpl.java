package com.enspy.csi.service.impl;

import com.enspy.csi.dto.response.RemboursementResponseDTO;
import com.enspy.csi.entity.FeuillemMaladie;
import com.enspy.csi.entity.Remboursement;
import com.enspy.csi.exception.ResourceNotFoundException;
import com.enspy.csi.repository.FeuillemMaladieRepository;
import com.enspy.csi.repository.PrescriptionRepository;
import com.enspy.csi.repository.RemboursementRepository;
import com.enspy.csi.service.RemboursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RemboursementServiceImpl implements RemboursementService {

    private final RemboursementRepository remboursementRepository;
    private final FeuillemMaladieRepository feuillemMaladieRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Override
    @Transactional
    public RemboursementResponseDTO effectuerRemboursement(Long feuilleMaladieId, String modePaiement) {
        FeuillemMaladie feuille = feuillemMaladieRepository.findById(feuilleMaladieId)
                .orElseThrow(() -> new ResourceNotFoundException("Feuille de maladie introuvable avec l'id : " + feuilleMaladieId));

        if (Boolean.TRUE.equals(feuille.getEstRembourse())) {
            throw new IllegalStateException("Cette feuille de maladie a déjà été remboursée");
        }

        if (feuille.getMontantSoin() == null || feuille.getMontantSoin() <= 0) {
            throw new IllegalStateException(
                    "Impossible de rembourser : le montant des soins de la feuille est invalide ou non défini.");
        }

        if (modePaiement == null) {
            throw new IllegalArgumentException("Mode de paiement invalide. Valeurs acceptées : VIREMENT, CASH");
        }
        String normalizedMode = modePaiement.toUpperCase();
        if (!"VIREMENT".equals(normalizedMode) && !"CASH".equals(normalizedMode)) {
            throw new IllegalArgumentException("Mode de paiement invalide. Valeurs acceptées : VIREMENT, CASH");
        }

        double taux = 1.0;
        if (feuille.getConsultation() != null) {
            Long consultationId = feuille.getConsultation().getId();
            var specialistPrescriptions = prescriptionRepository.findConsultationsByConsultationId(consultationId);
            if (!specialistPrescriptions.isEmpty()) {
                taux = 0.8;
            }
        }

        double montantRembourse = feuille.getMontantSoin() * taux;

        Remboursement remboursement = new Remboursement();
        remboursement.setMontant(montantRembourse);
        remboursement.setDateRemboursement(LocalDate.now());
        remboursement.setModePaiement(normalizedMode);
        remboursement.setFeuilleMaladie(feuille);

        feuille.setEstRembourse(true);
        feuillemMaladieRepository.save(feuille);

        Remboursement saved = remboursementRepository.save(remboursement);

        return toDTO(saved);
    }

    @Override
    public RemboursementResponseDTO getRemboursementById(Long id) {
        Remboursement r = remboursementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remboursement introuvable avec l'id : " + id));
        return toDTO(r);
    }

    @Override
    public Double getTotalRemboursements() {
        Double total = remboursementRepository.sumTotalRemboursements();
        return total != null ? total : 0.0;
    }

    private RemboursementResponseDTO toDTO(Remboursement r) {
        RemboursementResponseDTO dto = new RemboursementResponseDTO();
        dto.setId(r.getId());
        dto.setMontant(r.getMontant());
        dto.setDateRemboursement(r.getDateRemboursement());
        dto.setModePaiement(r.getModePaiement());

        FeuillemMaladie feuille = r.getFeuilleMaladie();
        if (feuille != null) {
            dto.setFeuilleMaladieId(feuille.getId());
            dto.setIdFeuille(feuille.getIdFeuille());
            dto.setMontantSoin(feuille.getMontantSoin());
            if (feuille.getConsultation() != null && feuille.getConsultation().getAssure() != null) {
                dto.setAssureNom(feuille.getConsultation().getAssure().getNom());
            }
        }
        return dto;
    }
}
