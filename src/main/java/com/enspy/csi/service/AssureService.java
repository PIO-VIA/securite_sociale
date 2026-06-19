package com.enspy.csi.service;

import com.enspy.csi.dto.request.AssureRequestDTO;
import com.enspy.csi.dto.response.AssureResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface AssureService {
    AssureResponseDTO inscrireAssure(AssureRequestDTO dto);
    AssureResponseDTO getAssureById(Long id);
    AssureResponseDTO getAssureByEmail(String email);
    AssureResponseDTO getAssureByIdAssure(String idAssure);
    List<AssureResponseDTO> getAllAssures();
    AssureResponseDTO updateAssure(Long id, AssureRequestDTO dto);
    void deleteAssure(Long id);
    AssureResponseDTO choisirMedecin(Long assureId, Long generalisteId);
    AssureResponseDTO uploadPhoto(Long id, MultipartFile photo);
    List<AssureResponseDTO> getAssuresByMedecinEmail(String email);
}
