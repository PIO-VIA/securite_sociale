package com.enspy.csi.controller;

import com.enspy.csi.service.RemboursementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/remboursements")
@RequiredArgsConstructor
@Tag(name = "Remboursements", description = "Gestion des remboursements")
public class RemboursementController {

    private final RemboursementService remboursementService;

    @PostMapping("/{feuilleMaladieId}")
    @Operation(summary = "Effectuer un remboursement")
    public ResponseEntity<?> effectuer(
            @PathVariable Long feuilleMaladieId,
            @RequestParam String modePaiement) {
        return ResponseEntity.ok(remboursementService.effectuerRemboursement(feuilleMaladieId, modePaiement));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un remboursement par ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(remboursementService.getRemboursementById(id));
    }
}
