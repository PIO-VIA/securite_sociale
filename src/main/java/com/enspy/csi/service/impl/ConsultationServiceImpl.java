package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.ConsultationRequestDTO;
import com.enspy.csi.dto.response.ConsultationResponseDTO;
import com.enspy.csi.repository.ConsultationRepository;
import com.enspy.csi.repository.AssureRepository;
import com.enspy.csi.repository.GeneralisteRepository;
import com.enspy.csi.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final AssureRepository assureRepository;
    private final GeneralisteRepository generalisteRepository;

    @Override
    public ConsultationResponseDTO creerConsultation(ConsultationRequestDTO dto) {
        throw new UnsupportedOperationException("TODO: implement creerConsultation");
    }

    @Override
    public ConsultationResponseDTO getConsultationById(Long id) {
        throw new UnsupportedOperationException("TODO: implement getConsultationById");
    }

    @Override
    public List<ConsultationResponseDTO> getConsultationsByAssure(Long assureId) {
        throw new UnsupportedOperationException("TODO: implement getConsultationsByAssure");
    }

    @Override
    public List<ConsultationResponseDTO> getConsultationsByGeneraliste(Long generalisteId) {
        throw new UnsupportedOperationException("TODO: implement getConsultationsByGeneraliste");
    }
}
