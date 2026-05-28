package com.enspy.csi.controller;

import com.enspy.csi.dto.request.FeuillemMaladieRequestDTO;
import com.enspy.csi.service.FeuillemMaladieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feuilles-maladie")
@RequiredArgsConstructor
@Tag(name = "Feuilles Maladie", description = "Gestion des feuilles de maladie")
public class FeuillemMaladieController {

    private final FeuillemMaladieService feuillemMaladieService;

    @PostMapping
    @Operation(summary = "Enregistrer une feuille de maladie")
    public ResponseEntity<?> enregistrer(@RequestBody FeuillemMaladieRequestDTO dto) {
        return ResponseEntity.ok(feuillemMaladieService.enregistrerFeuilleMaladie(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une feuille de maladie par ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(feuillemMaladieService.getFeuilleMaladieById(id));
    }

    @GetMapping
    @Operation(summary = "Lister toutes les feuilles de maladie")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(feuillemMaladieService.getAllFeuillesMaladie());
    }
}
