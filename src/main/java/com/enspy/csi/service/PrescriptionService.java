package com.enspy.csi.service;

import com.enspy.csi.dto.request.PrescriptionRequestDTO;
import com.enspy.csi.dto.response.PrescriptionResponseDTO;
import java.util.List;

public interface PrescriptionService {
    PrescriptionResponseDTO ajouterPrescriptionMedicament(PrescriptionRequestDTO dto);
    PrescriptionResponseDTO ajouterPrescriptionConsultation(PrescriptionRequestDTO dto);
    List<PrescriptionResponseDTO> getPrescriptionsByConsultation(Long consultationId);
    PrescriptionResponseDTO modifierPrescription(Long id, PrescriptionRequestDTO dto);
    void supprimerPrescription(Long id);
    List<PrescriptionResponseDTO> getPrescriptionsForSpecialiste(String matricule);
    List<PrescriptionResponseDTO> getPrescriptionsForSpecialisteEmail(String email);
}
