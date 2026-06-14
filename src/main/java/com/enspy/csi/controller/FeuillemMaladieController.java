package com.enspy.csi.controller;

import com.enspy.csi.dto.request.FeuillemMaladieRequestDTO;
import com.enspy.csi.service.FeuillemMaladieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feuilles-maladie")
@RequiredArgsConstructor
@Tag(name = "Feuilles Maladie", description = "Gestion des feuilles de maladie")
public class FeuillemMaladieController {

    private final FeuillemMaladieService feuillemMaladieService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ORGANISME', 'MEDECIN')")
    @Operation(summary = "Enregistrer une feuille de maladie")
    public ResponseEntity<?> enregistrer(@RequestBody FeuillemMaladieRequestDTO dto) {
        return ResponseEntity.ok(feuillemMaladieService.enregistrerFeuilleMaladie(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISME') or @securityService.isSelfAssureForFeuille(principal, #id)")
    @Operation(summary = "Récupérer une feuille de maladie par ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(feuillemMaladieService.getFeuilleMaladieById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Lister toutes les feuilles de maladie")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(feuillemMaladieService.getAllFeuillesMaladie());
    }

    @GetMapping("/assure/{assureId}")
    @PreAuthorize("hasRole('ORGANISME') or @securityService.isSelfAssure(principal, #assureId)")
    @Operation(summary = "Récupérer les feuilles de maladie d'un assuré")
    public ResponseEntity<?> getByAssure(@PathVariable Long assureId) {
        return ResponseEntity.ok(feuillemMaladieService.getFeuillesByAssure(assureId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANISME', 'MEDECIN')")
    @Operation(summary = "Modifier une feuille de maladie")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody FeuillemMaladieRequestDTO dto) {
        return ResponseEntity.ok(feuillemMaladieService.modifierFeuilleMaladie(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Supprimer une feuille de maladie")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        feuillemMaladieService.supprimerFeuilleMaladie(id);
        return ResponseEntity.noContent().build();
    }
}
