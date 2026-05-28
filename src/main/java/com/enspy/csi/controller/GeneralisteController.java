package com.enspy.csi.controller;

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

    @GetMapping
    @Operation(summary = "Lister tous les généralistes")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok("TODO: implement generalistes list");
    }
}
