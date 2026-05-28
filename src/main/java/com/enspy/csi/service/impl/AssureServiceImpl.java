package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.AssureRequestDTO;
import com.enspy.csi.dto.response.AssureResponseDTO;
import com.enspy.csi.repository.AssureRepository;
import com.enspy.csi.repository.GeneralisteRepository;
import com.enspy.csi.service.AssureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssureServiceImpl implements AssureService {

    private final AssureRepository assureRepository;
    private final GeneralisteRepository generalisteRepository;

    @Override
    public AssureResponseDTO inscrireAssure(AssureRequestDTO dto) {
        throw new UnsupportedOperationException("TODO: implement inscrireAssure");
    }

    @Override
    public AssureResponseDTO getAssureById(Long id) {
        throw new UnsupportedOperationException("TODO: implement getAssureById");
    }

    @Override
    public List<AssureResponseDTO> getAllAssures() {
        throw new UnsupportedOperationException("TODO: implement getAllAssures");
    }

    @Override
    public AssureResponseDTO updateAssure(Long id, AssureRequestDTO dto) {
        throw new UnsupportedOperationException("TODO: implement updateAssure");
    }

    @Override
    public void deleteAssure(Long id) {
        throw new UnsupportedOperationException("TODO: implement deleteAssure");
    }

    @Override
    public AssureResponseDTO choisirMedecin(Long assureId, Long generalisteId) {
        throw new UnsupportedOperationException("TODO: implement choisirMedecin");
    }
}
