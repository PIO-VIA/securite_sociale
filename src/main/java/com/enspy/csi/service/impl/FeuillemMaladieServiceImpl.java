package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.FeuillemMaladieRequestDTO;
import com.enspy.csi.dto.response.FeuillemMaladieResponseDTO;
import com.enspy.csi.entity.Consultation;
import com.enspy.csi.entity.FeuillemMaladie;
import com.enspy.csi.exception.ResourceNotFoundException;
import com.enspy.csi.repository.FeuillemMaladieRepository;
import com.enspy.csi.repository.ConsultationRepository;
import com.enspy.csi.service.FeuillemMaladieService;
import com.enspy.csi.service.RemboursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeuillemMaladieServiceImpl implements FeuillemMaladieService {

    private final FeuillemMaladieRepository feuillemMaladieRepository;
    private final ConsultationRepository consultationRepository;
    private final RemboursementService remboursementService;

    @Override
    @Transactional
    public FeuillemMaladieResponseDTO enregistrerFeuilleMaladie(FeuillemMaladieRequestDTO dto) {
        Consultation consultation = consultationRepository.findById(dto.getConsultationId())
                .orElseThrow(() -> new ResourceNotFoundException("Consultation introuvable avec l'id : " + dto.getConsultationId()));

        if (feuillemMaladieRepository.existsByConsultationId(dto.getConsultationId())) {
            throw new IllegalStateException("Une feuille de maladie existe déjà pour cette consultation");
        }

        if (dto.getMontantSoin() == null || dto.getMontantSoin() <= 0) {
            throw new IllegalArgumentException("Le montant des soins est obligatoire et doit être strictement positif.");
        }

        FeuillemMaladie fm = new FeuillemMaladie();
        fm.setIdFeuille(dto.getIdFeuille());
        fm.setMontantSoin(dto.getMontantSoin());
        fm.setEstRembourse(false);
        fm.setConsultation(consultation);

        FeuillemMaladie saved = feuillemMaladieRepository.save(fm);

        // Initie automatiquement le remboursement au statut EN_ATTENTE.
        // L'agent le confirmera ensuite (mode de paiement + passage au statut EFFECTUE).
        remboursementService.initierRemboursement(saved.getId());

        return toDTO(saved);
    }

    @Override
    public FeuillemMaladieResponseDTO getFeuilleMaladieById(Long id) {
        FeuillemMaladie fm = feuillemMaladieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feuille de maladie introuvable avec l'id : " + id));
        return toDTO(fm);
    }

    @Override
    public List<FeuillemMaladieResponseDTO> getAllFeuillesMaladie() {
        return feuillemMaladieRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeuillemMaladieResponseDTO> getFeuillesNonRemboursees() {
        return feuillemMaladieRepository.findByEstRembourse(false).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeuillemMaladieResponseDTO> getFeuillesByAssure(Long assureId) {
        return feuillemMaladieRepository.findByConsultationAssureId(assureId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeuillemMaladieResponseDTO> getFeuillesByMedecinEmail(String email) {
        return feuillemMaladieRepository.findByConsultationGeneralisteEmail(email).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FeuillemMaladieResponseDTO modifierFeuilleMaladie(Long id, FeuillemMaladieRequestDTO dto) {
        FeuillemMaladie fm = feuillemMaladieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feuille de maladie introuvable avec l'id : " + id));

        if (dto.getIdFeuille() != null) {
            fm.setIdFeuille(dto.getIdFeuille());
        }
        if (dto.getMontantSoin() != null) {
            if (dto.getMontantSoin() <= 0) {
                throw new IllegalArgumentException("Le montant des soins doit être strictement positif.");
            }
            fm.setMontantSoin(dto.getMontantSoin());
        }
        if (dto.getConsultationId() != null) {
            if (fm.getConsultation() == null || !fm.getConsultation().getId().equals(dto.getConsultationId())) {
                Consultation consultation = consultationRepository.findById(dto.getConsultationId())
                        .orElseThrow(() -> new ResourceNotFoundException("Consultation introuvable avec l'id : " + dto.getConsultationId()));
                if (feuillemMaladieRepository.existsByConsultationId(dto.getConsultationId())) {
                    throw new IllegalStateException("Une feuille de maladie existe déjà pour cette consultation");
                }
                fm.setConsultation(consultation);
            }
        }

        FeuillemMaladie saved = feuillemMaladieRepository.save(fm);
        return toDTO(saved);
    }

    @Override
    public void supprimerFeuilleMaladie(Long id) {
        FeuillemMaladie fm = feuillemMaladieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feuille de maladie introuvable avec l'id : " + id));
        feuillemMaladieRepository.delete(fm);
    }

    private FeuillemMaladieResponseDTO toDTO(FeuillemMaladie fm) {
        FeuillemMaladieResponseDTO dto = new FeuillemMaladieResponseDTO();
        dto.setId(fm.getId());
        dto.setIdFeuille(fm.getIdFeuille());
        dto.setMontantSoin(fm.getMontantSoin());
        dto.setEstRembourse(fm.getEstRembourse());

        if (fm.getConsultation() != null) {
            dto.setConsultationId(fm.getConsultation().getId());
            dto.setConsultationDate(fm.getConsultation().getDate());
            if (fm.getConsultation().getAssure() != null) {
                dto.setAssureId(fm.getConsultation().getAssure().getId());
                dto.setAssureNom(fm.getConsultation().getAssure().getNom());
                dto.setAssureIdAssure(fm.getConsultation().getAssure().getIdAssure());
            }
        }

        if (fm.getRemboursement() != null) {
            dto.setMontantRembourse(fm.getRemboursement().getMontant());
        }
        return dto;
    }
}
