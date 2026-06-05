package com.enspy.csi.controller;

import com.enspy.csi.repository.AssureRepository;
import com.enspy.csi.service.MedecinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/generalistes")
@RequiredArgsConstructor
@Tag(name = "Généralistes", description = "Gestion spécifique des médecins généralistes")
public class GeneralisteController {

    private final MedecinService medecinService;
    private final AssureRepository assureRepository;

    @GetMapping
    @Operation(summary = "Lister tous les généralistes")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(medecinService.getAllMedecins()
                .stream()
                .filter(m -> "GENERALISTE".equals(m.getType()))
                .toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un généraliste par ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medecinService.getMedecinById(id));
    }

    @GetMapping("/{id}/assures")
    @Operation(summary = "Lister les assurés d'un généraliste")
    public ResponseEntity<?> getAssuresByGeneraliste(@PathVariable Long id) {
        return ResponseEntity.ok(assureRepository.findByMedecinTraitantId(id));
    }
}
