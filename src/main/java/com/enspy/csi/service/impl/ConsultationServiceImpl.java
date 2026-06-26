package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.ConsultationRequestDTO;
import com.enspy.csi.dto.response.ConsultationResponseDTO;
import com.enspy.csi.entity.Assure;
import com.enspy.csi.entity.Consultation;
import com.enspy.csi.entity.Generaliste;
import com.enspy.csi.entity.Medecin;
import com.enspy.csi.entity.Specialiste;
import com.enspy.csi.repository.ConsultationRepository;
import com.enspy.csi.repository.AssureRepository;
import com.enspy.csi.repository.GeneralisteRepository;
import com.enspy.csi.repository.MedecinRepository;
import com.enspy.csi.repository.PrescriptionRepository;
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
    private final MedecinRepository medecinRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Override
    public ConsultationResponseDTO creerConsultation(ConsultationRequestDTO dto) {
        Assure assure = assureRepository.findById(dto.getAssureId()).orElseThrow(() -> new IllegalArgumentException("Assure introuvable avec l'ID: "+dto.getAssureId()));
        Medecin medecin = medecinRepository.findById(dto.getGeneralisteId()).orElseThrow(() -> new IllegalArgumentException("Medecin introuvable avec l'ID: "+dto.getGeneralisteId()));

        if (medecin instanceof Generaliste generaliste) {
            if (assure.getMedecinTraitant() == null || !assure.getMedecinTraitant().getId().equals(generaliste.getId())){
                // Violation de règle métier : renvoie 400 BAD REQUEST (pas 500)
                throw new IllegalArgumentException("Erreur: L'assure doit consulter son medecin traitant");
            }
        } else if (medecin instanceof Specialiste specialiste) {
            boolean hasPrescription = prescriptionRepository.findConsultationsByMatriculeMedecin(specialiste.getMatricule())
                    .stream()
                    .anyMatch(p -> p.getConsultation() != null
                            && p.getConsultation().getAssure() != null
                            && p.getConsultation().getAssure().getId().equals(assure.getId()));
            if (!hasPrescription) {
                throw new IllegalArgumentException("Erreur: Ce spécialiste n'a pas reçu de prescription pour cet assuré");
            }
        } else {
            throw new IllegalArgumentException("Erreur: Type de médecin non reconnu");
        }

        Consultation consultation = new Consultation();
        consultation.setAssure(assure);
        consultation.setGeneraliste(medecin);
        consultation.setMotif(dto.getMotif());

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
        Medecin medecin = medecinRepository.findById(generalisteId)
                .orElseThrow(() -> new IllegalArgumentException("Médecin dont l'id est " + generalisteId + " introuvable"));
        if (medecin instanceof Specialiste) {
            throw new IllegalArgumentException("Erreur: L'ID fourni correspond à un spécialiste, pas à un généraliste.");
        }
        List<Consultation> consultations = consultationRepository.findByGeneralisteId(generalisteId);
        return consultations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ConsultationResponseDTO> getConsultationsBySpecialiste(Long specialisteId) {
        Medecin medecin = medecinRepository.findById(specialisteId)
                .orElseThrow(() -> new IllegalArgumentException("Médecin dont l'id est " + specialisteId + " introuvable"));
        if (medecin instanceof Generaliste) {
            throw new IllegalArgumentException("Erreur: L'ID fourni correspond à un généraliste, pas à un spécialiste.");
        }
        List<Consultation> consultations = consultationRepository.findByGeneralisteId(specialisteId);
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
            dto.setAssureNom(consultationSauvegardee.getAssure().getNom());
            dto.setAssureIdAssure(consultationSauvegardee.getAssure().getIdAssure());
        }

        if (consultationSauvegardee.getGeneraliste() != null) {
            dto.setGeneralisteId(consultationSauvegardee.getGeneraliste().getId());
            dto.setGeneralisteNom(consultationSauvegardee.getGeneraliste().getNom());
            dto.setGeneralisteMatricule(consultationSauvegardee.getGeneraliste().getMatricule());
        }

        dto.setNombrePrescriptions(
                consultationSauvegardee.getPrescriptions() != null ? consultationSauvegardee.getPrescriptions().size() : 0);
        dto.setPossedeFeuilleMaladie(consultationSauvegardee.getFeuilleMaladie() != null);
        dto.setMotif(consultationSauvegardee.getMotif());

        return dto;
    }
}
