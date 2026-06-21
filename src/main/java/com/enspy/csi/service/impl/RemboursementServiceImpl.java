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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RemboursementServiceImpl implements RemboursementService {

    private final RemboursementRepository remboursementRepository;
    private final FeuillemMaladieRepository feuillemMaladieRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Override
    @Transactional
    public RemboursementResponseDTO initierRemboursement(Long feuilleMaladieId) {
        FeuillemMaladie feuille = feuillemMaladieRepository.findById(feuilleMaladieId)
                .orElseThrow(() -> new ResourceNotFoundException("Feuille de maladie introuvable avec l'id : " + feuilleMaladieId));

        // Idempotent : si un remboursement existe déjà pour cette feuille, on le renvoie
        Remboursement existant = remboursementRepository.findByFeuilleMaladieId(feuilleMaladieId).orElse(null);
        if (existant != null) {
            return toDTO(existant);
        }

        Remboursement remboursement = new Remboursement();
        remboursement.setMontant(calculerMontant(feuille));
        remboursement.setStatut(Remboursement.STATUT_EN_ATTENTE);
        remboursement.setFeuilleMaladie(feuille);

        Remboursement saved = remboursementRepository.save(remboursement);
        feuille.setRemboursement(saved);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public RemboursementResponseDTO confirmerRemboursement(Long feuilleMaladieId, String modePaiement) {
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

        // Récupère le remboursement initié à la création de la feuille, ou le crée si absent (robustesse)
        Remboursement remboursement = remboursementRepository.findByFeuilleMaladieId(feuilleMaladieId)
                .orElseGet(() -> {
                    Remboursement r = new Remboursement();
                    r.setStatut(Remboursement.STATUT_EN_ATTENTE);
                    r.setFeuilleMaladie(feuille);
                    return r;
                });

        if (Remboursement.STATUT_EFFECTUE.equals(remboursement.getStatut())) {
            throw new IllegalStateException("Ce remboursement a déjà été confirmé.");
        }

        // Recalcule le montant final (reflète une éventuelle orientation spécialiste ajoutée depuis)
        remboursement.setMontant(calculerMontant(feuille));
        remboursement.setModePaiement(normalizedMode);
        remboursement.setDateRemboursement(LocalDate.now());
        remboursement.setStatut(Remboursement.STATUT_EFFECTUE);

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
    public RemboursementResponseDTO getByFeuilleMaladieId(Long feuilleMaladieId) {
        Remboursement r = remboursementRepository.findByFeuilleMaladieId(feuilleMaladieId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aucun remboursement trouvé pour la feuille de maladie id : " + feuilleMaladieId));
        return toDTO(r);
    }

    @Override
    public List<RemboursementResponseDTO> getRemboursementsEnAttente() {
        return remboursementRepository.findByStatut(Remboursement.STATUT_EN_ATTENTE).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Double getTotalRemboursements() {
        Double total = remboursementRepository.sumTotalRemboursements();
        return total != null ? total : 0.0;
    }

    /**
     * Calcule le montant remboursable d'une feuille : 100% par défaut,
     * 80% si la consultation comporte une orientation vers un spécialiste.
     */
    private double calculerMontant(FeuillemMaladie feuille) {
        double taux = 1.0;
        if (feuille.getConsultation() != null) {
            Long consultationId = feuille.getConsultation().getId();
            var specialistPrescriptions = prescriptionRepository.findConsultationsByConsultationId(consultationId);
            if (!specialistPrescriptions.isEmpty()) {
                taux = 0.8;
            }
        }
        Double montantSoin = feuille.getMontantSoin();
        return (montantSoin != null ? montantSoin : 0.0) * taux;
    }

    private RemboursementResponseDTO toDTO(Remboursement r) {
        RemboursementResponseDTO dto = new RemboursementResponseDTO();
        dto.setId(r.getId());
        dto.setMontant(r.getMontant());
        dto.setDateRemboursement(r.getDateRemboursement());
        dto.setModePaiement(r.getModePaiement());
        dto.setStatut(r.getStatut());

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

    @Override
    @Transactional
    public void actualiserMontantRemboursement(Long feuilleMaladieId) {
        remboursementRepository.findByFeuilleMaladieId(feuilleMaladieId).ifPresent(remboursement -> {
            if (Remboursement.STATUT_EN_ATTENTE.equals(remboursement.getStatut())) {
                remboursement.setMontant(calculerMontant(remboursement.getFeuilleMaladie()));
                remboursementRepository.save(remboursement);
            }
        });
    }
}
