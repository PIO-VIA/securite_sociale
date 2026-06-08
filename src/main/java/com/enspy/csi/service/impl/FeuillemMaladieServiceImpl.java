package com.enspy.csi.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enspy.csi.dto.request.FeuillemMaladieRequestDTO;
import com.enspy.csi.dto.response.FeuillemMaladieResponseDTO;
import com.enspy.csi.entity.Consultation;
import com.enspy.csi.entity.FeuillemMaladie;
import com.enspy.csi.exception.ResourceNotFoundException;
import com.enspy.csi.repository.ConsultationRepository;
import com.enspy.csi.repository.FeuillemMaladieRepository;
import com.enspy.csi.service.FeuillemMaladieService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeuillemMaladieServiceImpl implements FeuillemMaladieService {

    private final FeuillemMaladieRepository feuillemMaladieRepository;
    private final ConsultationRepository consultationRepository;

    @Override
    @Transactional
    public FeuillemMaladieResponseDTO enregistrerFeuilleMaladie(FeuillemMaladieRequestDTO dto) {
        // Récupérer la consultation (exception si absente)
        Consultation consultation = consultationRepository.findById(dto.getConsultationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Consultation introuvable avec l'id : " + dto.getConsultationId()));

        // Vérifier qu'aucune feuille de maladie n'existe déjà pour cette consultation
        if (feuillemMaladieRepository.existsByConsultationId(dto.getConsultationId())) {
            throw new IllegalStateException(
                    "Une feuille de maladie existe déjà pour cette consultation");
        }

        // Créer la feuille de maladie
        FeuillemMaladie feuille = new FeuillemMaladie();
        feuille.setIdFeuille(dto.getIdFeuille());
        feuille.setMontantSoin(dto.getMontantSoin());
        feuille.setEstRembourse(false);
        feuille.setConsultation(consultation);

        FeuillemMaladie saved = feuillemMaladieRepository.save(feuille);
        return toDTO(saved);
    }

    @Override
    public FeuillemMaladieResponseDTO getFeuilleMaladieById(Long id) {
        FeuillemMaladie feuille = feuillemMaladieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Feuille de maladie introuvable avec l'id : " + id));
        return toDTO(feuille);
    }

    @Override
    public List<FeuillemMaladieResponseDTO> getAllFeuillesMaladie() {
        return feuillemMaladieRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeuillemMaladieResponseDTO> getFeuillesNonRemboursees() {
        return feuillemMaladieRepository.findByEstRembourse(false)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- Méthode de mapping privée ---
    private FeuillemMaladieResponseDTO toDTO(FeuillemMaladie f) {
        FeuillemMaladieResponseDTO dto = new FeuillemMaladieResponseDTO();
        dto.setId(f.getId());
        dto.setIdFeuille(f.getIdFeuille());
        dto.setMontantSoin(f.getMontantSoin());
        dto.setEstRembourse(f.getEstRembourse());
        dto.setConsultationId(f.getConsultation() != null ? f.getConsultation().getId() : null);
        return dto;
    }
}