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
import com.enspy.csi.service.MedecinService;
import com.enspy.csi.util.PasswordGenerator;
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
    private final EmailService emailService;
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
    public List<MedecinResponseDTO> getAllMedecins() {
        return medecinRepository.findAll().stream()
                .map(m -> toDTO(m, m instanceof Generaliste ? "GENERALISTE" : "SPECIALISTE"))
                .collect(Collectors.toList());
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

    private void remplirChampsCommuns(Medecin medecin, MedecinRequestDTO dto) {
        medecin.setNom(dto.getNom());
        medecin.setDateNaissance(dto.getDateNaissance());
        medecin.setSexe(dto.getSexe());
        medecin.setIndicatifPays(dto.getIndicatifPays());
        medecin.setNumTelephone(dto.getNumTelephone());
        medecin.setMatricule(dto.getMatricule());
        medecin.setEstAssure(dto.getEstAssure() != null ? dto.getEstAssure() : false);
        medecin.setEmail(dto.getEmail());
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
        dto.setMatricule(medecin.getMatricule());
        dto.setEstAssure(medecin.getEstAssure());
        dto.setType(type);
        dto.setEmail(medecin.getEmail());
        if (medecin instanceof Specialiste s) {
            dto.setDomaineSpecialisation(s.getDomaineSpecialisation());
        }
        return dto;
    }
}
