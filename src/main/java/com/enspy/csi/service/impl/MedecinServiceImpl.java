package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.ChangePasswordRequestDTO;
import com.enspy.csi.dto.request.MedecinRequestDTO;
import com.enspy.csi.dto.response.MedecinResponseDTO;
import com.enspy.csi.entity.Generaliste;
import com.enspy.csi.entity.Medecin;
import com.enspy.csi.entity.Specialiste;
import com.enspy.csi.exception.ResourceNotFoundException;
import com.enspy.csi.repository.GeneralisteRepository;
import com.enspy.csi.repository.MedecinRepository;
import com.enspy.csi.repository.SpecialisteRepository;
import com.enspy.csi.service.EmailService;
import com.enspy.csi.service.FileStorageService;
import com.enspy.csi.service.MedecinService;
import com.enspy.csi.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedecinServiceImpl implements MedecinService {

    private final MedecinRepository medecinRepository;
    private final GeneralisteRepository generalisteRepository;
    private final SpecialisteRepository specialisteRepository;
    private final EmailService emailService;
    private final FileStorageService fileStorageService;
    private final @org.springframework.context.annotation.Lazy org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public MedecinResponseDTO enregistrerMedecin(MedecinRequestDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("L'email est obligatoire pour créer un médecin.");
        }

        String type = dto.getType();
        if (type == null) {
            throw new IllegalArgumentException("Type médecin invalide. Valeurs acceptées : GENERALISTE, SPECIALISTE");
        }

        String motDePasseGenere = PasswordGenerator.generate();

        return switch (type.toUpperCase()) {
            case "GENERALISTE" -> {
                Generaliste g = new Generaliste();
                remplirChampsCommuns(g, dto);
                g.setMotDePasse(passwordEncoder.encode(motDePasseGenere));
                Generaliste saved = generalisteRepository.save(g);
                envoyerIdentifiants(saved, motDePasseGenere);
                yield toDTO(saved, "GENERALISTE");
            }
            case "SPECIALISTE" -> {
                Specialiste s = new Specialiste();
                remplirChampsCommuns(s, dto);
                s.setDomaineSpecialisation(dto.getDomaineSpecialisation());
                s.setMotDePasse(passwordEncoder.encode(motDePasseGenere));
                Specialiste saved = specialisteRepository.save(s);
                envoyerIdentifiants(saved, motDePasseGenere);
                yield toDTO(saved, "SPECIALISTE");
            }
            default -> throw new IllegalArgumentException(
                    "Type médecin invalide. Valeurs acceptées : GENERALISTE, SPECIALISTE");
        };
    }

    @Override
    public void changerMotDePasse(String username, ChangePasswordRequestDTO dto) {
        Medecin medecin = medecinRepository.findByEmail(username)
                .or(() -> medecinRepository.findByMatricule(username))
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable."));

        if (dto.getAncienMotDePasse() == null || dto.getNouveauMotDePasse() == null) {
            throw new IllegalArgumentException("L'ancien et le nouveau mot de passe sont obligatoires.");
        }
        if (dto.getNouveauMotDePasse().length() < 6) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 6 caractères.");
        }
        if (medecin.getMotDePasse() == null
                || !passwordEncoder.matches(dto.getAncienMotDePasse(), medecin.getMotDePasse())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect.");
        }

        medecin.setMotDePasse(passwordEncoder.encode(dto.getNouveauMotDePasse()));
        medecinRepository.save(medecin);
    }

    @Override
    public MedecinResponseDTO getMedecinById(Long id) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable avec l'id : " + id));
        return toDTO(medecin, medecin instanceof Generaliste ? "GENERALISTE" : "SPECIALISTE");
    }

    @Override
    public MedecinResponseDTO getMedecinByEmail(String email) {
        Medecin medecin = medecinRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable avec l'email : " + email));
        return toDTO(medecin, medecin instanceof Generaliste ? "GENERALISTE" : "SPECIALISTE");
    }

    @Override
    public MedecinResponseDTO uploadPhoto(Long id, MultipartFile photo) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable avec l'id : " + id));
        String url = fileStorageService.stockerImage(photo, "medecins");
        if (url != null) {
            if (medecin.getPhotoUrl() != null) {
                fileStorageService.supprimer(medecin.getPhotoUrl());
            }
            medecin.setPhotoUrl(url);
            medecinRepository.save(medecin);
        }
        return toDTO(medecin, medecin instanceof Generaliste ? "GENERALISTE" : "SPECIALISTE");
    }

    @Override
    public List<MedecinResponseDTO> getAllMedecins() {
        return medecinRepository.findAll().stream()
                .map(m -> toDTO(m, m instanceof Generaliste ? "GENERALISTE" : "SPECIALISTE"))
                .collect(Collectors.toList());
    }

    @Override
    public MedecinResponseDTO modifierMedecin(Long id, MedecinRequestDTO dto) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable avec l'id : " + id));

        if (dto.getNom() != null) medecin.setNom(dto.getNom());
        if (dto.getDateNaissance() != null) medecin.setDateNaissance(dto.getDateNaissance());
        if (dto.getSexe() != null) medecin.setSexe(dto.getSexe());
        if (dto.getIndicatifPays() != null) medecin.setIndicatifPays(dto.getIndicatifPays());
        if (dto.getNumTelephone() != null) medecin.setNumTelephone(dto.getNumTelephone());
        if (dto.getMatricule() != null) medecin.setMatricule(dto.getMatricule());
        if (dto.getEstAssure() != null) medecin.setEstAssure(dto.getEstAssure());
        if (dto.getEmail() != null) medecin.setEmail(dto.getEmail());
        if (dto.getMotDePasse() != null && !dto.getMotDePasse().isBlank()) {
            medecin.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        }

        if (dto.getPhotoUrl() != null) medecin.setPhotoUrl(dto.getPhotoUrl());

        if (medecin instanceof Specialiste && dto.getDomaineSpecialisation() != null) {
            ((Specialiste) medecin).setDomaineSpecialisation(dto.getDomaineSpecialisation());
        }

        Medecin saved = medecinRepository.save(medecin);
        return toDTO(saved, saved instanceof Generaliste ? "GENERALISTE" : "SPECIALISTE");
    }

    @Override
    public void supprimerMedecin(Long id) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable avec l'id : " + id));

        if (medecin instanceof Generaliste) {
            generalisteRepository.deleteById(id);
        } else if (medecin instanceof Specialiste) {
            specialisteRepository.deleteById(id);
        } else {
            medecinRepository.deleteById(id);
        }
    }

    @Override
    public MedecinResponseDTO resetMotDePasse(Long id) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable avec l'id : " + id));

        String nouveauMotDePasse = PasswordGenerator.generate();
        medecin.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        medecinRepository.save(medecin);

        // Envoie le nouveau mot de passe par email
        try {
            emailService.envoyerMotDePasseMedecin(medecin.getEmail(), medecin.getNom(), nouveauMotDePasse);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Mot de passe réinitialisé en DB mais l'envoi de l'email a échoué : " + e.getMessage());
        }

        return toDTO(medecin, medecin instanceof Generaliste ? "GENERALISTE" : "SPECIALISTE");
    }

    private void remplirChampsCommuns(Medecin medecin, MedecinRequestDTO dto) {
        medecin.setNom(dto.getNom());
        medecin.setDateNaissance(dto.getDateNaissance());
        medecin.setSexe(dto.getSexe());
        medecin.setIndicatifPays(dto.getIndicatifPays());
        medecin.setNumTelephone(dto.getNumTelephone());
        medecin.setMatricule(dto.getMatricule());
        medecin.setEstAssure(dto.getEstAssure() != null ? dto.getEstAssure() : false);
        medecin.setEmail(dto.getEmail());
        medecin.setPhotoUrl(dto.getPhotoUrl());
    }

    private void envoyerIdentifiants(Medecin medecin, String motDePasse) {
        try {
            emailService.envoyerMotDePasseMedecin(medecin.getEmail(), medecin.getNom(), motDePasse);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Médecin enregistré mais l'envoi de l'email a échoué : " + e.getMessage());
        }
    }

    private MedecinResponseDTO toDTO(Medecin medecin, String type) {
        MedecinResponseDTO dto = new MedecinResponseDTO();
        dto.setId(medecin.getId());
        dto.setNom(medecin.getNom());
        dto.setDateNaissance(medecin.getDateNaissance());
        dto.setSexe(medecin.getSexe());
        dto.setIndicatifPays(medecin.getIndicatifPays());
        dto.setNumTelephone(medecin.getNumTelephone());
        dto.setMatricule(medecin.getMatricule());
        dto.setEstAssure(medecin.getEstAssure());
        dto.setType(type);
        dto.setEmail(medecin.getEmail());
        dto.setPhotoUrl(medecin.getPhotoUrl());
        if (medecin instanceof Specialiste s) {
            dto.setDomaineSpecialisation(s.getDomaineSpecialisation());
        }
        return dto;
    }
}
