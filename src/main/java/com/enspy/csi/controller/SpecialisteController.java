package com.enspy.csi.controller;

import com.enspy.csi.repository.SpecialisteRepository;
import com.enspy.csi.service.MedecinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/specialistes")
@RequiredArgsConstructor
@Tag(name = "Spécialistes", description = "Gestion spécifique des médecins spécialistes")
public class SpecialisteController {

    private final MedecinService medecinService;
    private final SpecialisteRepository specialisteRepository;

    @GetMapping
    @Operation(summary = "Lister tous les spécialistes")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(medecinService.getAllMedecins()
                .stream()
                .filter(m -> "SPECIALISTE".equals(m.getType()))
                .toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un spécialiste par ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(medecinService.getMedecinById(id));
    }

    @GetMapping("/domaine/{domaine}")
    @Operation(summary = "Filtrer les spécialistes par domaine")
    public ResponseEntity<?> getByDomaine(@PathVariable String domaine) {
        return ResponseEntity.ok(specialisteRepository.findByDomaineSpecialisation(domaine));
    }
}
