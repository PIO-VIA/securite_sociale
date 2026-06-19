package com.enspy.csi.controller;

import com.enspy.csi.dto.request.MedecinRequestDTO;
import com.enspy.csi.service.AssureService;
import com.enspy.csi.service.MedecinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/medecins")
@RequiredArgsConstructor
@Tag(name = "Médecins", description = "Gestion globale des médecins")
public class MedecinController {

    private final MedecinService medecinService;
    private final AssureService assureService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Enregistrer un nouveau médecin")
    public ResponseEntity<?> enregistrer(@RequestBody MedecinRequestDTO dto) {
        return ResponseEntity.ok(medecinService.enregistrerMedecin(dto));
    }

    @GetMapping("/me/assures")
    @PreAuthorize("hasRole('MEDECIN')")
    @Operation(summary = "Lister les assurés affectés au médecin connecté")
    public ResponseEntity<?> getMesAssures(Authentication authentication) {
        return ResponseEntity.ok(assureService.getAssuresByMedecinEmail(authentication.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANISME', 'MEDECIN', 'ASSURE')")
    @Operation(summary = "Récupérer un médecin par ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medecinService.getMedecinById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ORGANISME', 'MEDECIN', 'ASSURE')")
    @Operation(summary = "Lister tous les médecins")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(medecinService.getAllMedecins());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Modifier un médecin par ID")
    public ResponseEntity<?> modifier(@PathVariable Long id, @RequestBody MedecinRequestDTO dto) {
        return ResponseEntity.ok(medecinService.modifierMedecin(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Supprimer un médecin par ID")
    public ResponseEntity<?> supprimer(@PathVariable Long id) {
        medecinService.supprimerMedecin(id);
        return ResponseEntity.ok("Médecin supprimé avec succès.");
    }

    @PatchMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Réinitialiser le mot de passe d'un médecin (envoie un nouveau par email)")
    public ResponseEntity<?> resetPassword(@PathVariable Long id) {
        return ResponseEntity.ok(medecinService.resetMotDePasse(id));
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ORGANISME', 'MEDECIN')")
    @Operation(summary = "Téléverser/mettre à jour la photo de profil d'un médecin (optionnel)")
    public ResponseEntity<?> uploadPhoto(@PathVariable Long id, @RequestParam("photo") MultipartFile photo) {
        return ResponseEntity.ok(medecinService.uploadPhoto(id, photo));
    }
}
