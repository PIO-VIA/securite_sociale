package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.FeuillemMaladieRequestDTO;
import com.enspy.csi.dto.response.FeuillemMaladieResponseDTO;
import com.enspy.csi.entity.Consultation;
import com.enspy.csi.entity.FeuillemMaladie;
import com.enspy.csi.exception.ResourceNotFoundException;
import com.enspy.csi.repository.FeuillemMaladieRepository;
import com.enspy.csi.repository.ConsultationRepository;
import com.enspy.csi.service.FeuillemMaladieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeuillemMaladieServiceImpl implements FeuillemMaladieService {

    private final FeuillemMaladieRepository feuillemMaladieRepository;
    private final ConsultationRepository consultationRepository;

    @Override
    public FeuillemMaladieResponseDTO enregistrerFeuilleMaladie(FeuillemMaladieRequestDTO dto) {
        Consultation consultation = consultationRepository.findById(dto.getConsultationId())
                .orElseThrow(() -> new ResourceNotFoundException("Consultation introuvable avec l'id : " + dto.getConsultationId()));

        if (feuillemMaladieRepository.existsByConsultationId(dto.getConsultationId())) {
            throw new IllegalStateException("Une feuille de maladie existe déjà pour cette consultation");
        }

        FeuillemMaladie fm = new FeuillemMaladie();
        fm.setIdFeuille(dto.getIdFeuille());
        fm.setMontantSoin(dto.getMontantSoin());
        fm.setEstRembourse(false);
        fm.setConsultation(consultation);

        FeuillemMaladie saved = feuillemMaladieRepository.save(fm);
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

    private FeuillemMaladieResponseDTO toDTO(FeuillemMaladie fm) {
        FeuillemMaladieResponseDTO dto = new FeuillemMaladieResponseDTO();
        dto.setId(fm.getId());
        dto.setIdFeuille(fm.getIdFeuille());
        dto.setMontantSoin(fm.getMontantSoin());
        dto.setEstRembourse(fm.getEstRembourse());
        dto.setConsultationId(fm.getConsultation() != null ? fm.getConsultation().getId() : null);
        return dto;
    }
}
