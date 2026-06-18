package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.AssureRequestDTO;
import com.enspy.csi.dto.response.AssureResponseDTO;
import com.enspy.csi.entity.Assure;
import com.enspy.csi.entity.Generaliste;
import com.enspy.csi.exception.ResourceNotFoundException;
import com.enspy.csi.repository.AssureRepository;
import com.enspy.csi.repository.GeneralisteRepository;
import com.enspy.csi.service.AssureService;
import com.enspy.csi.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssureServiceImpl implements AssureService {

    private final AssureRepository assureRepository;
    private final GeneralisteRepository generalisteRepository;
    private final FileStorageService fileStorageService;
    private final @org.springframework.context.annotation.Lazy org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

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
        assure.setEmail(dto.getEmail());
        assure.setPhotoUrl(dto.getPhotoUrl());
        if (dto.getMotDePasse() != null) {
            assure.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        }
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
    public AssureResponseDTO getAssureByEmail(String email) {
        Assure assure = assureRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Assuré introuvable avec l'email : " + email));
        return toDTO(assure);
    }

    @Override
    public AssureResponseDTO getAssureByIdAssure(String idAssure) {
        Assure assure = assureRepository.findByIdAssure(idAssure)
                .orElseThrow(() -> new ResourceNotFoundException("Assuré introuvable avec l'identifiant : " + idAssure));
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
        if (dto.getEmail() != null) assure.setEmail(dto.getEmail());
        if (dto.getPhotoUrl() != null) assure.setPhotoUrl(dto.getPhotoUrl());
        if (dto.getMotDePasse() != null) assure.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
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

    @Override
    public AssureResponseDTO uploadPhoto(Long id, MultipartFile photo) {
        Assure assure = assureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assuré introuvable avec l'id : " + id));
        String url = fileStorageService.stockerImage(photo, "assures");
        if (url != null) {
            if (assure.getPhotoUrl() != null) {
                fileStorageService.supprimer(assure.getPhotoUrl());
            }
            assure.setPhotoUrl(url);
            assureRepository.save(assure);
        }
        return toDTO(assure);
    }

    private AssureResponseDTO toDTO(Assure assure) {
        AssureResponseDTO dto = new AssureResponseDTO();
        dto.setId(assure.getId());
        dto.setIdAssure(assure.getIdAssure());
        dto.setNom(assure.getNom());
        dto.setDateNaissance(assure.getDateNaissance());
        dto.setSexe(assure.getSexe());
        dto.setIndicatifPays(assure.getIndicatifPays());
        dto.setNumTelephone(assure.getNumTelephone());
        dto.setProfession(assure.getProfession());
        dto.setStatutMatrimoniale(assure.getStatutMatrimoniale());
        dto.setGroupeSanguin(assure.getGroupeSanguin());
        dto.setEmail(assure.getEmail());
        dto.setDateInscription(assure.getDateInscription());
        dto.setPhotoUrl(assure.getPhotoUrl());

        if (assure.getMedecinTraitant() != null) {
            dto.setMedecinTraitantId(assure.getMedecinTraitant().getId());
            dto.setMedecinTraitantNom(assure.getMedecinTraitant().getNom());
            dto.setMedecinTraitantMatricule(assure.getMedecinTraitant().getMatricule());
        }

        dto.setNombreConsultations(assure.getConsultations() != null ? assure.getConsultations().size() : 0);
        return dto;
    }
}
