package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.MedecinRequestDTO;
import com.enspy.csi.dto.response.MedecinResponseDTO;
import com.enspy.csi.repository.MedecinRepository;
import com.enspy.csi.repository.GeneralisteRepository;
import com.enspy.csi.repository.SpecialisteRepository;
import com.enspy.csi.service.MedecinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedecinServiceImpl implements MedecinService {

    private final MedecinRepository medecinRepository;
    private final GeneralisteRepository generalisteRepository;
    private final SpecialisteRepository specialisteRepository;

    @Override
    public MedecinResponseDTO enregistrerMedecin(MedecinRequestDTO dto) {
        throw new UnsupportedOperationException("TODO: implement enregistrerMedecin");
    }

    @Override
    public MedecinResponseDTO getMedecinById(Long id) {
        throw new UnsupportedOperationException("TODO: implement getMedecinById");
    }

    @Override
    public List<MedecinResponseDTO> getAllMedecins() {
        throw new UnsupportedOperationException("TODO: implement getAllMedecins");
    }
}
