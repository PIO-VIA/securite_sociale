package com.enspy.csi.controller;

import com.enspy.csi.dto.request.AssureRequestDTO;
import com.enspy.csi.service.AssureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assures")
@RequiredArgsConstructor
@Tag(name = "Assurés", description = "Gestion des assurés")
public class AssureController {

    private final AssureService assureService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Inscrire un nouvel assuré")
    public ResponseEntity<?> inscrire(@RequestBody AssureRequestDTO dto) {
        return ResponseEntity.ok(assureService.inscrireAssure(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANISME', 'MEDECIN') or @securityService.isSelfAssure(principal, #id)")
    @Operation(summary = "Récupérer un assuré par ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assureService.getAssureById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Lister tous les assurés")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(assureService.getAllAssures());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Modifier un assuré")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AssureRequestDTO dto) {
        return ResponseEntity.ok(assureService.updateAssure(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Supprimer un assuré")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        assureService.deleteAssure(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{assureId}/choisir-medecin/{generalisteId}")
    @PreAuthorize("hasRole('ORGANISME') or @securityService.isSelfAssure(principal, #assureId)")
    @Operation(summary = "Choisir son médecin traitant")
    public ResponseEntity<?> choisirMedecin(
            @PathVariable Long assureId,
            @PathVariable Long generalisteId) {
        return ResponseEntity.ok(assureService.choisirMedecin(assureId, generalisteId));
    }
}
