package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.PrescriptionRequestDTO;
import com.enspy.csi.dto.response.PrescriptionResponseDTO;
import com.enspy.csi.entity.Consultation;
import com.enspy.csi.entity.Prescription;
import com.enspy.csi.entity.PrescriptionConsultation;
import com.enspy.csi.entity.PrescriptionMedicament;
import com.enspy.csi.entity.Medecin;
import com.enspy.csi.entity.Assure;
import com.enspy.csi.exception.ResourceNotFoundException;
import com.enspy.csi.repository.PrescriptionRepository;
import com.enspy.csi.repository.ConsultationRepository;
import com.enspy.csi.repository.MedecinRepository;
import com.enspy.csi.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final ConsultationRepository consultationRepository;
    private final MedecinRepository medecinRepository;

    @Override
    public PrescriptionResponseDTO ajouterPrescriptionMedicament(PrescriptionRequestDTO dto) {
        Consultation consultation = consultationRepository.findById(dto.getConsultationId())
                .orElseThrow(() -> new IllegalArgumentException("Consultation introuvable avec l'ID : " + dto.getConsultationId()));

        PrescriptionMedicament pm = new PrescriptionMedicament();
        pm.setConsultation(consultation);
        pm.setMedicament(dto.getMedicament());
        pm.setPosologie(dto.getPosologie());

        PrescriptionMedicament sauvegade = prescriptionRepository.save(pm);
        return toDTO(sauvegade);

    }

    @Override
    public PrescriptionResponseDTO ajouterPrescriptionConsultation(PrescriptionRequestDTO dto) {
        Consultation consultation = consultationRepository.findById(dto.getConsultationId())
                .orElseThrow(() -> new IllegalArgumentException("Consultation introuvable avec l'ID : " + dto.getConsultationId()));

        if (dto.getMatriculeMedecin() == null || dto.getMatriculeMedecin().trim().isEmpty()) {
            throw new IllegalArgumentException("Le matricule du médecin spécialiste est obligatoire pour une orientation.");
        }

        PrescriptionConsultation pc = new PrescriptionConsultation();
        pc.setConsultation(consultation);
        pc.setMatriculeMedecin(dto.getMatriculeMedecin());
        pc.setMotif(dto.getMotif());

        PrescriptionConsultation sauvegarde = prescriptionRepository.save(pc);

        return toDTO(sauvegarde);
    }

    @Override
    public List<PrescriptionResponseDTO> getPrescriptionsByConsultation(Long consultationId) {
        if (!consultationRepository.existsById(consultationId)) {
            throw new IllegalArgumentException("Consultation introuvable avec l'ID : " + consultationId);
        }


        List<Prescription> prescriptions = prescriptionRepository.findByConsultationId(consultationId);


        return prescriptions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private PrescriptionResponseDTO toDTO(Prescription p) {
        PrescriptionResponseDTO dto = new PrescriptionResponseDTO();
        dto.setId(p.getId());
        dto.setConsultationId(p.getConsultation() != null ? p.getConsultation().getId() : null);

        if (p instanceof PrescriptionMedicament) {
            PrescriptionMedicament pm = (PrescriptionMedicament) p;
            dto.setType("MEDICAMENT");
            dto.setMedicament(pm.getMedicament());
            dto.setPosologie(pm.getPosologie());
        }
        else if (p instanceof PrescriptionConsultation pc) {
            dto.setType("CONSULTATION_SPECIALISTE");
            dto.setMatriculeMedecin(pc.getMatriculeMedecin());
            dto.setMotif(pc.getMotif());
            if (pc.getConsultation() != null) {
                dto.setDateConsultation(pc.getConsultation().getDate());
                if (pc.getConsultation().getGeneraliste() != null) {
                    dto.setMedecinPrescripteurNom(pc.getConsultation().getGeneraliste().getNom());
                }
                if (pc.getConsultation().getAssure() != null) {
                    Assure assure = pc.getConsultation().getAssure();
                    PrescriptionResponseDTO.AssureMinResponseDTO assureMin = new PrescriptionResponseDTO.AssureMinResponseDTO();
                    assureMin.setId(assure.getId());
                    assureMin.setIdAssure(assure.getIdAssure());
                    assureMin.setNom(assure.getNom());
                    assureMin.setNumTelephone(assure.getNumTelephone());
                    assureMin.setSexe(assure.getSexe());
                    assureMin.setGroupeSanguin(assure.getGroupeSanguin());
                    dto.setAssure(assureMin);
                }
            }
        }

        return dto;
    }

    @Override
    public PrescriptionResponseDTO modifierPrescription(Long id, PrescriptionRequestDTO dto) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription introuvable avec l'ID : " + id));

        if (dto.getConsultationId() != null) {
            Consultation consultation = consultationRepository.findById(dto.getConsultationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Consultation introuvable avec l'ID : " + dto.getConsultationId()));
            p.setConsultation(consultation);
        }

        if (p instanceof PrescriptionMedicament) {
            PrescriptionMedicament pm = (PrescriptionMedicament) p;
            if (dto.getMedicament() != null) {
                pm.setMedicament(dto.getMedicament());
            }
            if (dto.getPosologie() != null) {
                pm.setPosologie(dto.getPosologie());
            }
        } else if (p instanceof PrescriptionConsultation) {
            PrescriptionConsultation pc = (PrescriptionConsultation) p;
            if (dto.getMatriculeMedecin() != null) {
                if (dto.getMatriculeMedecin().trim().isEmpty()) {
                    throw new IllegalArgumentException("Le matricule du médecin spécialiste est obligatoire pour une orientation.");
                }
                pc.setMatriculeMedecin(dto.getMatriculeMedecin());
            }
            if (dto.getMotif() != null) {
                pc.setMotif(dto.getMotif());
            }
        }

        Prescription saved = prescriptionRepository.save(p);
        return toDTO(saved);
    }

    @Override
    public void supprimerPrescription(Long id) {
        Prescription p = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription introuvable avec l'ID : " + id));
        prescriptionRepository.delete(p);
    }

    @Override
    public List<PrescriptionResponseDTO> getPrescriptionsForSpecialiste(String matricule) {
        List<PrescriptionConsultation> consultations = prescriptionRepository.findConsultationsByMatriculeMedecin(matricule);
        return consultations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionResponseDTO> getPrescriptionsForSpecialisteEmail(String email) {
        Medecin medecin = medecinRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable avec l'email : " + email));
        return getPrescriptionsForSpecialiste(medecin.getMatricule());
    }

}

