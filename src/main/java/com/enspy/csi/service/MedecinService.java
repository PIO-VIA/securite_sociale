package com.enspy.csi.service;

import com.enspy.csi.dto.request.ChangePasswordRequestDTO;
import com.enspy.csi.dto.request.MedecinRequestDTO;
import com.enspy.csi.dto.response.MedecinResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface MedecinService {
    MedecinResponseDTO enregistrerMedecin(MedecinRequestDTO dto);
    MedecinResponseDTO getMedecinById(Long id);
    MedecinResponseDTO getMedecinByEmail(String email);
    List<MedecinResponseDTO> getAllMedecins();
    MedecinResponseDTO modifierMedecin(Long id, MedecinRequestDTO dto);
    void changerMotDePasse(String username, ChangePasswordRequestDTO dto);
    void supprimerMedecin(Long id);
    MedecinResponseDTO resetMotDePasse(Long id);
    MedecinResponseDTO uploadPhoto(Long id, MultipartFile photo);
}
