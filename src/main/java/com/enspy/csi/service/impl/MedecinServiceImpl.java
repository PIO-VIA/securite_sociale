package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.MedecinRequestDTO;
import com.enspy.csi.dto.response.MedecinResponseDTO;
import com.enspy.csi.entity.Generaliste;
import com.enspy.csi.entity.Medecin;
import com.enspy.csi.entity.Specialiste;
import com.enspy.csi.exception.ResourceNotFoundException;
import com.enspy.csi.repository.GeneralisteRepository;
import com.enspy.csi.repository.MedecinRepository;
import com.enspy.csi.repository.SpecialisteRepository;
import com.enspy.csi.service.MedecinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedecinServiceImpl implements MedecinService {

    private final MedecinRepository medecinRepository;
    private final GeneralisteRepository generalisteRepository;
    private final SpecialisteRepository specialisteRepository;

    @Override
    public MedecinResponseDTO enregistrerMedecin(MedecinRequestDTO dto) {
        String type = dto.getType();
        if (type == null) {
            throw new IllegalArgumentException("Type médecin invalide. Valeurs acceptées : GENERALISTE, SPECIALISTE");
        }
        return switch (type.toUpperCase()) {
            case "GENERALISTE" -> {
                Generaliste g = new Generaliste();
                g.setNom(dto.getNom());
                g.setDateNaissance(dto.getDateNaissance());
                g.setSexe(dto.getSexe());
                g.setIndicatifPays(dto.getIndicatifPays());
                g.setNumTelephone(dto.getNumTelephone());
                g.setMatricule(dto.getMatricule());
                g.setEstAssure(dto.getEstAssure() != null ? dto.getEstAssure() : false);
                yield toDTO(generalisteRepository.save(g), "GENERALISTE");
            }
            case "SPECIALISTE" -> {
                Specialiste s = new Specialiste();
                s.setNom(dto.getNom());
                s.setDateNaissance(dto.getDateNaissance());
                s.setSexe(dto.getSexe());
                s.setIndicatifPays(dto.getIndicatifPays());
                s.setNumTelephone(dto.getNumTelephone());
                s.setMatricule(dto.getMatricule());
                s.setEstAssure(dto.getEstAssure() != null ? dto.getEstAssure() : false);
                s.setDomaineSpecialisation(dto.getDomaineSpecialisation());
                yield toDTO(specialisteRepository.save(s), "SPECIALISTE");
            }
            default -> throw new IllegalArgumentException(
                    "Type médecin invalide. Valeurs acceptées : GENERALISTE, SPECIALISTE");
        };
    }

    @Override
    public MedecinResponseDTO getMedecinById(Long id) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable avec l'id : " + id));
        return toDTO(medecin, medecin instanceof Generaliste ? "GENERALISTE" : "SPECIALISTE");
    }

    @Override
    public List<MedecinResponseDTO> getAllMedecins() {
        return medecinRepository.findAll().stream()
                .map(m -> toDTO(m, m instanceof Generaliste ? "GENERALISTE" : "SPECIALISTE"))
                .collect(Collectors.toList());
    }

    private MedecinResponseDTO toDTO(Medecin medecin, String type) {
        MedecinResponseDTO dto = new MedecinResponseDTO();
        dto.setId(medecin.getId());
        dto.setNom(medecin.getNom());
        dto.setMatricule(medecin.getMatricule());
        dto.setEstAssure(medecin.getEstAssure());
        dto.setType(type);
        if (medecin instanceof Specialiste s) {
            dto.setDomaineSpecialisation(s.getDomaineSpecialisation());
        }
        return dto;
    }
}
