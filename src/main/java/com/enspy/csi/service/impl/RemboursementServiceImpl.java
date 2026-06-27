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
        remboursement.getFeuillesMaladie().add(feuille);

        Remboursement saved = remboursementRepository.save(remboursement);
        feuille.setRemboursement(saved);
        feuillemMaladieRepository.save(feuille);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public RemboursementResponseDTO initierRemboursementPourFeuilles(List<Long> feuilleMaladieIds) {
        if (feuilleMaladieIds == null || feuilleMaladieIds.isEmpty()) {
            throw new IllegalArgumentException("La liste des IDs de feuilles de maladie ne doit pas être vide.");
        }

        List<FeuillemMaladie> feuilles = feuillemMaladieRepository.findAllById(feuilleMaladieIds);
        if (feuilles.size() != feuilleMaladieIds.size()) {
            throw new ResourceNotFoundException("Une ou plusieurs feuilles de maladie sont introuvables.");
        }

        for (FeuillemMaladie f : feuilles) {
            if (Boolean.TRUE.equals(f.getEstRembourse())) {
                throw new IllegalStateException("La feuille " + f.getIdFeuille() + " a déjà été remboursée.");
            }
        }

        Remboursement remboursement = new Remboursement();
        remboursement.setStatut(Remboursement.STATUT_EN_ATTENTE);
        remboursement.setFeuillesMaladie(new java.util.ArrayList<>());

        java.util.Set<Remboursement> anciensADelete = new java.util.HashSet<>();
        for (FeuillemMaladie f : feuilles) {
            Remboursement ancien = f.getRemboursement();
            if (ancien != null) {
                if (Remboursement.STATUT_EN_ATTENTE.equals(ancien.getStatut())) {
                    anciensADelete.add(ancien);
                } else {
                    f.setRemboursement(null);
                }
            }
        }

        for (Remboursement ancien : anciensADelete) {
            for (FeuillemMaladie other : new java.util.ArrayList<>(ancien.getFeuillesMaladie())) {
                other.setRemboursement(null);
                feuillemMaladieRepository.save(other);
            }
            ancien.getFeuillesMaladie().clear();
            remboursementRepository.delete(ancien);
        }

        for (FeuillemMaladie f : feuilles) {
            f.setRemboursement(remboursement);
            remboursement.getFeuillesMaladie().add(f);
        }

        double totalMontant = 0.0;
        for (FeuillemMaladie f : feuilles) {
            totalMontant += calculerMontant(f);
        }
        remboursement.setMontant(totalMontant);

        Remboursement saved = remboursementRepository.save(remboursement);
        feuillemMaladieRepository.saveAll(feuilles);

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

        if (modePaiement == null) {
            throw new IllegalArgumentException("Mode de paiement invalide. Valeurs acceptées : VIREMENT, CASH");
        }
        String normalizedMode = modePaiement.toUpperCase();
        if (!"VIREMENT".equals(normalizedMode) && !"CASH".equals(normalizedMode)) {
            throw new IllegalArgumentException("Mode de paiement invalide. Valeurs acceptées : VIREMENT, CASH");
        }

        Remboursement remboursement = remboursementRepository.findByFeuilleMaladieId(feuilleMaladieId)
                .orElseGet(() -> {
                    Remboursement r = new Remboursement();
                    r.setStatut(Remboursement.STATUT_EN_ATTENTE);
                    r.setMontant(0.0);
                    // Sauvegarder d'abord pour obtenir un ID et éviter TransientPropertyValueException
                    r = remboursementRepository.save(r);
                    r.getFeuillesMaladie().add(feuille);
                    feuille.setRemboursement(r);
                    return r;
                });

        if (Remboursement.STATUT_EFFECTUE.equals(remboursement.getStatut())) {
            throw new IllegalStateException("Ce remboursement a déjà été confirmé.");
        }

        double totalMontant = 0.0;
        for (FeuillemMaladie f : remboursement.getFeuillesMaladie()) {
            if (f.getMontantSoin() == null || f.getMontantSoin() <= 0) {
                throw new IllegalStateException(
                        "Impossible de rembourser : le montant des soins de la feuille " + f.getIdFeuille() + " est invalide.");
            }
            totalMontant += calculerMontant(f);
            f.setEstRembourse(true);
            feuillemMaladieRepository.save(f);
        }

        remboursement.setMontant(totalMontant);
        remboursement.setModePaiement(normalizedMode);
        remboursement.setDateRemboursement(LocalDate.now());
        remboursement.setStatut(Remboursement.STATUT_EFFECTUE);

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

        List<FeuillemMaladie> feuilles = r.getFeuillesMaladie();
        if (feuilles != null && !feuilles.isEmpty()) {
            FeuillemMaladie first = feuilles.get(0);
            dto.setFeuilleMaladieId(first.getId());
            dto.setIdFeuille(first.getIdFeuille());
            
            java.util.List<Long> ids = feuilles.stream()
                    .map(FeuillemMaladie::getId)
                    .collect(Collectors.toList());
            dto.setFeuilleMaladieIds(ids);

            double totalMontantSoin = feuilles.stream()
                    .mapToDouble(f -> f.getMontantSoin() != null ? f.getMontantSoin() : 0.0)
                    .sum();
            dto.setMontantSoin(totalMontantSoin);

            if (first.getConsultation() != null && first.getConsultation().getAssure() != null) {
                dto.setAssureNom(first.getConsultation().getAssure().getNom());
            }
        }
        return dto;
    }

    @Override
    @Transactional
    public void actualiserMontantRemboursement(Long feuilleMaladieId) {
        remboursementRepository.findByFeuilleMaladieId(feuilleMaladieId).ifPresent(remboursement -> {
            if (Remboursement.STATUT_EN_ATTENTE.equals(remboursement.getStatut())) {
                double total = 0.0;
                for (FeuillemMaladie f : remboursement.getFeuillesMaladie()) {
                    total += calculerMontant(f);
                }
                remboursement.setMontant(total);
                remboursementRepository.save(remboursement);
            }
        });
    }
}
