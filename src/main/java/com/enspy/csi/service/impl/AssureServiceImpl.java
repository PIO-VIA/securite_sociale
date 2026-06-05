package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.AssureRequestDTO;
import com.enspy.csi.dto.response.AssureResponseDTO;
import com.enspy.csi.entity.Assure;
import com.enspy.csi.entity.Generaliste;
import com.enspy.csi.exception.ResourceNotFoundException;
import com.enspy.csi.repository.AssureRepository;
import com.enspy.csi.repository.GeneralisteRepository;
import com.enspy.csi.service.AssureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssureServiceImpl implements AssureService {

    private final AssureRepository assureRepository;
    private final GeneralisteRepository generalisteRepository;

    @Override
    public AssureResponseDTO inscrireAssure(AssureRequestDTO dto) {
        Assure assure = new Assure();
        assure.setNom(dto.getNom());
        assure.setDateNaissance(dto.getDateNaissance());
        assure.setSexe(dto.getSexe());
        assure.setIndicatifPays(dto.getIndicatifPays());
        assure.setNumTelephone(dto.getNumTelephone());
        assure.setProfession(dto.getProfession());
        assure.setStatutMatrimoniale(dto.getStatutMatrimoniale());
        assure.setGroupeSanguin(dto.getGroupeSanguin());
        assure.setIdAssure("ASS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        assure.setDateInscription(LocalDate.now());
        return toDTO(assureRepository.save(assure));
    }

    @Override
    public AssureResponseDTO getAssureById(Long id) {
        Assure assure = assureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assuré introuvable avec l'id : " + id));
        return toDTO(assure);
    }

    @Override
    public List<AssureResponseDTO> getAllAssures() {
        return assureRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AssureResponseDTO updateAssure(Long id, AssureRequestDTO dto) {
        Assure assure = assureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assuré introuvable avec l'id : " + id));
        if (dto.getNom() != null) assure.setNom(dto.getNom());
        if (dto.getDateNaissance() != null) assure.setDateNaissance(dto.getDateNaissance());
        if (dto.getSexe() != null) assure.setSexe(dto.getSexe());
        if (dto.getIndicatifPays() != null) assure.setIndicatifPays(dto.getIndicatifPays());
        if (dto.getNumTelephone() != null) assure.setNumTelephone(dto.getNumTelephone());
        if (dto.getProfession() != null) assure.setProfession(dto.getProfession());
        if (dto.getStatutMatrimoniale() != null) assure.setStatutMatrimoniale(dto.getStatutMatrimoniale());
        if (dto.getGroupeSanguin() != null) assure.setGroupeSanguin(dto.getGroupeSanguin());
        return toDTO(assureRepository.save(assure));
    }

    @Override
    public void deleteAssure(Long id) {
        if (!assureRepository.existsById(id)) {
            throw new ResourceNotFoundException("Assuré introuvable avec l'id : " + id);
        }
        assureRepository.deleteById(id);
    }

    @Override
    public AssureResponseDTO choisirMedecin(Long assureId, Long generalisteId) {
        Assure assure = assureRepository.findById(assureId)
                .orElseThrow(() -> new ResourceNotFoundException("Assuré introuvable avec l'id : " + assureId));
        Generaliste generaliste = generalisteRepository.findById(generalisteId)
                .orElseThrow(() -> new ResourceNotFoundException("Généraliste introuvable avec l'id : " + generalisteId));
        assure.setMedecinTraitant(generaliste);
        return toDTO(assureRepository.save(assure));
    }

    private AssureResponseDTO toDTO(Assure assure) {
        AssureResponseDTO dto = new AssureResponseDTO();
        dto.setId(assure.getId());
        dto.setIdAssure(assure.getIdAssure());
        dto.setNom(assure.getNom());
        dto.setDateNaissance(assure.getDateNaissance());
        dto.setProfession(assure.getProfession());
        dto.setGroupeSanguin(assure.getGroupeSanguin());
        return dto;
    }
}
