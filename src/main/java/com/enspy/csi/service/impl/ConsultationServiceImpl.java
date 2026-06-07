package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.ConsultationRequestDTO;
import com.enspy.csi.dto.response.ConsultationResponseDTO;
import com.enspy.csi.entity.Assure;
import com.enspy.csi.entity.Consultation;
import com.enspy.csi.entity.Generaliste;
import com.enspy.csi.repository.ConsultationRepository;
import com.enspy.csi.repository.AssureRepository;
import com.enspy.csi.repository.GeneralisteRepository;
import com.enspy.csi.service.ConsultationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final AssureRepository assureRepository;
    private final GeneralisteRepository generalisteRepository;

    @Override
    public ConsultationResponseDTO creerConsultation(ConsultationRequestDTO dto) {
        Assure assure = assureRepository.findById(dto.getAssureId()).orElseThrow(() -> new IllegalArgumentException("Assure introuvable avec l'ID: "+dto.getAssureId()));
        Generaliste generaliste = generalisteRepository.findById(dto.getGeneralisteId()).orElseThrow(() -> new IllegalArgumentException("Medecin generaliste introuvable avec l'ID: "+dto.getGeneralisteId()));

        if (assure.getMedecinTraitant() == null || !assure.getMedecinTraitant().getId().equals(generaliste.getId())){
            throw new IllegalStateException("Erreur: L'assure doit consulter son medecin traitant");
        }
        Consultation consultation = new Consultation();
        consultation.setAssure(assure);
        consultation.setGeneraliste(generaliste);

        if(dto.getDate() != null){
            consultation.setDate(dto.getDate());
        }else{
            consultation.setDate(LocalDate.now());
        }
        Consultation consultationSauvegardee = consultationRepository.save(consultation);

        return  toDTO(consultationSauvegardee);
    }

    @Override
    public ConsultationResponseDTO getConsultationById(Long id) {
        Consultation consultation = consultationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Consultation introuvable avec l'Id :" +id));
        return  toDTO(consultation);
    }

    @Override
    public List<ConsultationResponseDTO> getConsultationsByAssure(Long assureId) {
        if (!assureRepository.existsById(assureId)){
            throw new IllegalArgumentException("Assure dont l'id est " +assureId+ " introuvable" );
        }
        List<Consultation> consultations = consultationRepository.findByAssureId(assureId);
        return consultations.stream()
                            .map(this::toDTO)
                            .collect(Collectors.toList());
    }

    @Override
    public List<ConsultationResponseDTO> getConsultationsByGeneraliste(Long generalisteId) {
        if (!assureRepository.existsById(generalisteId)){
            throw new IllegalArgumentException("Assure dont l'id est " +generalisteId+ " introuvable" );
        }
        List<Consultation> consultations = consultationRepository.findByAssureId(generalisteId);
        return consultations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ConsultationResponseDTO toDTO(Consultation consultationSauvegardee) {
        ConsultationResponseDTO dto = new ConsultationResponseDTO();

        dto.setId(consultationSauvegardee.getId());
        dto.setDate(consultationSauvegardee.getDate());

        if (consultationSauvegardee.getAssure() != null) {
            dto.setAssureId(consultationSauvegardee.getAssure().getId());
        } else {
            dto.setAssureId(null);
        }

        if (consultationSauvegardee.getGeneraliste() != null) {
            dto.setGeneralisteId(consultationSauvegardee.getGeneraliste().getId());
        } else {
            dto.setGeneralisteId(null);
        }

        return dto;
    }
}
