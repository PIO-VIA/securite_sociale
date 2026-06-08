package com.enspy.csi.controller;

import com.enspy.csi.service.FeuillemMaladieService;
import com.enspy.csi.service.RemboursementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/remboursements")
@RequiredArgsConstructor
@Tag(name = "Remboursements", description = "Gestion des remboursements")
public class RemboursementController {

    private final RemboursementService remboursementService;
    private final FeuillemMaladieService feuillemMaladieService;

    @PostMapping("/{feuilleMaladieId}")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Effectuer un remboursement")
    public ResponseEntity<?> effectuer(
            @PathVariable Long feuilleMaladieId,
            @RequestParam String modePaiement) {
        return ResponseEntity.ok(remboursementService.effectuerRemboursement(feuilleMaladieId, modePaiement));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISME') or @securityService.isSelfAssureForRemboursement(principal, #id)")
    @Operation(summary = "Récupérer un remboursement par ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(remboursementService.getRemboursementById(id));
    }

    @GetMapping("/stats/total")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Montant total de tous les remboursements")
    public ResponseEntity<?> getTotal() {
        return ResponseEntity.ok(remboursementService.getTotalRemboursements());
    }

    @GetMapping("/non-rembourses")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Lister les feuilles de maladie non remboursées")
    public ResponseEntity<?> getNonRembourses() {
        return ResponseEntity.ok(feuillemMaladieService.getFeuillesNonRemboursees());
    }
}
