package com.enspy.csi.service;

import com.enspy.csi.dto.request.ConsultationRequestDTO;
import com.enspy.csi.dto.response.ConsultationResponseDTO;
import java.util.List;

public interface ConsultationService {
    ConsultationResponseDTO creerConsultation(ConsultationRequestDTO dto);
    ConsultationResponseDTO getConsultationById(Long id);
    List<ConsultationResponseDTO> getConsultationsByAssure(Long assureId);
    List<ConsultationResponseDTO> getConsultationsByGeneraliste(Long generalisteId);
}
