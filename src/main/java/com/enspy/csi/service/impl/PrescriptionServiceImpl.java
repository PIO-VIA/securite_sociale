package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.PrescriptionRequestDTO;
import com.enspy.csi.dto.response.PrescriptionResponseDTO;
import com.enspy.csi.repository.PrescriptionRepository;
import com.enspy.csi.repository.ConsultationRepository;
import com.enspy.csi.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final ConsultationRepository consultationRepository;

    @Override
    public PrescriptionResponseDTO ajouterPrescriptionMedicament(PrescriptionRequestDTO dto) {
        throw new UnsupportedOperationException("TODO: implement ajouterPrescriptionMedicament");
    }

    @Override
    public PrescriptionResponseDTO ajouterPrescriptionConsultation(PrescriptionRequestDTO dto) {
        throw new UnsupportedOperationException("TODO: implement ajouterPrescriptionConsultation");
    }

    @Override
    public List<PrescriptionResponseDTO> getPrescriptionsByConsultation(Long consultationId) {
        throw new UnsupportedOperationException("TODO: implement getPrescriptionsByConsultation");
    }
}
