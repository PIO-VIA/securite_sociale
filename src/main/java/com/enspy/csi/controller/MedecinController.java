package com.enspy.csi.controller;

import com.enspy.csi.dto.request.MedecinRequestDTO;
import com.enspy.csi.service.MedecinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medecins")
@RequiredArgsConstructor
@Tag(name = "Médecins", description = "Gestion globale des médecins")
public class MedecinController {

    private final MedecinService medecinService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Enregistrer un nouveau médecin")
    public ResponseEntity<?> enregistrer(@RequestBody MedecinRequestDTO dto) {
        return ResponseEntity.ok(medecinService.enregistrerMedecin(dto));
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Supprimer un médecin par ID")
    public ResponseEntity<?> supprimer(@PathVariable Long id) {
        medecinService.supprimerMedecin(id);
        return ResponseEntity.ok("Médecin supprimé avec succès.");
    }
}
