package com.enspy.csi.controller;

import com.enspy.csi.entity.Medecin;
import com.enspy.csi.entity.Specialiste;
import com.enspy.csi.exception.ResourceNotFoundException;
import com.enspy.csi.repository.AssureRepository;
import com.enspy.csi.repository.MedecinRepository;
import com.enspy.csi.repository.SpecialisteRepository;
import com.enspy.csi.service.MedecinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/specialistes")
@RequiredArgsConstructor
@Tag(name = "Spécialistes", description = "Gestion spécifique des médecins spécialistes")
public class SpecialisteController {

    private final MedecinService medecinService;
    private final SpecialisteRepository specialisteRepository;
    private final MedecinRepository medecinRepository;
    private final AssureRepository assureRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ORGANISME', 'MEDECIN', 'ASSURE')")
    @Operation(summary = "Lister tous les spécialistes")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(medecinService.getAllMedecins()
                .stream()
                .filter(m -> "SPECIALISTE".equals(m.getType()))
                .toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANISME', 'MEDECIN', 'ASSURE')")
    @Operation(summary = "Récupérer un spécialiste par ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medecinService.getMedecinById(id));
    }

    @GetMapping("/domaine/{domaine}")
    @PreAuthorize("hasAnyRole('ORGANISME', 'MEDECIN', 'ASSURE')")
    @Operation(summary = "Filtrer les spécialistes par domaine")
    public ResponseEntity<?> getByDomaine(@PathVariable String domaine) {
        return ResponseEntity.ok(specialisteRepository.findByDomaineSpecialisation(domaine));
    }

    @GetMapping("/{id}/assures")
    @PreAuthorize("hasRole('ORGANISME') or @securityService.isSelfGeneraliste(principal, #id)")
    @Operation(summary = "Lister les assurés attribués à un spécialiste par son ID")
    public ResponseEntity<?> getAssuresBySpecialiste(@PathVariable Long id) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable avec l'id : " + id));
        if (!(medecin instanceof Specialiste specialiste)) {
            return ResponseEntity.badRequest().body("Erreur: L'ID fourni correspond à un généraliste, pas à un spécialiste.");
        }
        return ResponseEntity.ok(assureRepository.findAssuresBySpecialisteMatricule(specialiste.getMatricule()));
    }

    @GetMapping("/me/assures")
    @PreAuthorize("hasRole('MEDECIN')")
    @Operation(summary = "Lister les assurés attribués au spécialiste connecté")
    public ResponseEntity<?> getMyAssures(Authentication authentication) {
        Medecin medecin = medecinRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable."));
        if (!(medecin instanceof Specialiste specialiste)) {
            return ResponseEntity.badRequest().body("Erreur: Vous n'êtes pas un médecin spécialiste.");
        }
        return ResponseEntity.ok(assureRepository.findAssuresBySpecialisteMatricule(specialiste.getMatricule()));
    }
}
