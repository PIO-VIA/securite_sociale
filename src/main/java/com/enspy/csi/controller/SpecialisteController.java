package com.enspy.csi.controller;

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

    @GetMapping
    @Operation(summary = "Lister tous les spécialistes")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok("TODO: implement specialistes list");
    }
}
