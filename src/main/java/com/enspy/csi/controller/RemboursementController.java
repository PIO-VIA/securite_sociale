package com.enspy.csi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.enspy.csi.dto.response.RemboursementResponseDTO;
import com.enspy.csi.service.FeuillemMaladieService;
import com.enspy.csi.service.RemboursementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/remboursements")
@RequiredArgsConstructor
@Tag(name = "Remboursements", description = "Gestion des remboursements")
public class RemboursementController {

    private final RemboursementService remboursementService;
    private final FeuillemMaladieService feuillemMaladieService;

    @PostMapping("/{feuilleMaladieId}")
    @Operation(summary = "Effectuer un remboursement pour une feuille de maladie")
    public ResponseEntity<?> effectuerRemboursement(
            @PathVariable Long feuilleMaladieId,
            @RequestParam String modePaiement) {
        RemboursementResponseDTO result = remboursementService.effectuerRemboursement(feuilleMaladieId, modePaiement);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un remboursement par ID")
    public ResponseEntity<?> getRemboursementById(@PathVariable Long id) {
        return ResponseEntity.ok(remboursementService.getRemboursementById(id));
    }

    @GetMapping("/stats/total")
    @Operation(summary = "Montant total de tous les remboursements")
    public ResponseEntity<?> getTotal() {
        return ResponseEntity.ok(remboursementService.getTotalRemboursements());
    }

    @GetMapping("/non-rembourses")
    @Operation(summary = "Lister les feuilles de maladie non remboursées")
    public ResponseEntity<?> getNonRembourses() {
        return ResponseEntity.ok(feuillemMaladieService.getFeuillesNonRemboursees());
    }
}