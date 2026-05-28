package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.FeuillemMaladieRequestDTO;
import com.enspy.csi.dto.response.FeuillemMaladieResponseDTO;
import com.enspy.csi.repository.FeuillemMaladieRepository;
import com.enspy.csi.repository.ConsultationRepository;
import com.enspy.csi.service.FeuillemMaladieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeuillemMaladieServiceImpl implements FeuillemMaladieService {

    private final FeuillemMaladieRepository feuillemMaladieRepository;
    private final ConsultationRepository consultationRepository;

    @Override
    public FeuillemMaladieResponseDTO enregistrerFeuilleMaladie(FeuillemMaladieRequestDTO dto) {
        throw new UnsupportedOperationException("TODO: implement enregistrerFeuilleMaladie");
    }

    @Override
    public FeuillemMaladieResponseDTO getFeuilleMaladieById(Long id) {
        throw new UnsupportedOperationException("TODO: implement getFeuilleMaladieById");
    }

    @Override
    public List<FeuillemMaladieResponseDTO> getAllFeuillesMaladie() {
        throw new UnsupportedOperationException("TODO: implement getAllFeuillesMaladie");
    }
}
