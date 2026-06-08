package com.enspy.csi.service;

import com.enspy.csi.dto.request.FeuillemMaladieRequestDTO;
import com.enspy.csi.dto.response.FeuillemMaladieResponseDTO;
import java.util.List;

public interface FeuillemMaladieService {
    FeuillemMaladieResponseDTO enregistrerFeuilleMaladie(FeuillemMaladieRequestDTO dto);
    FeuillemMaladieResponseDTO getFeuilleMaladieById(Long id);
    List<FeuillemMaladieResponseDTO> getAllFeuillesMaladie();
    List<FeuillemMaladieResponseDTO> getFeuillesNonRemboursees();
    List<FeuillemMaladieResponseDTO> getFeuillesByAssure(Long assureId);
}
