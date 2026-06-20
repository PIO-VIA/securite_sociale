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

    @PatchMapping("/{feuilleMaladieId}/confirmer")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Confirmer le remboursement d'une feuille (agent) : définit le mode de paiement et passe le statut à EFFECTUE")
    public ResponseEntity<?> confirmer(
            @PathVariable Long feuilleMaladieId,
            @RequestParam String modePaiement) {
        return ResponseEntity.ok(remboursementService.confirmerRemboursement(feuilleMaladieId, modePaiement));
    }

    @GetMapping("/en-attente")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Lister les remboursements en attente de confirmation")
    public ResponseEntity<?> getEnAttente() {
        return ResponseEntity.ok(remboursementService.getRemboursementsEnAttente());
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
