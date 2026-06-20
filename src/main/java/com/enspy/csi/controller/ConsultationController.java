package com.enspy.csi.controller;

import com.enspy.csi.dto.request.ConsultationRequestDTO;
import com.enspy.csi.service.ConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
@Tag(name = "Consultations", description = "Gestion des consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ORGANISME', 'MEDECIN')")
    @Operation(summary = "Créer une consultation")
    public ResponseEntity<?> creer(@RequestBody ConsultationRequestDTO dto) {
        return ResponseEntity.ok(consultationService.creerConsultation(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANISME','MEDECIN') or @securityService.isConsultationParticipant(principal, #id)")
    @Operation(summary = "Récupérer les détails d'une consultation")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.getConsultationById(id));
    }

    @GetMapping("/assure/{id}")
    @PreAuthorize("hasAnyRole('ORGANISME', 'MEDECIN') or @securityService.isSelfAssure(principal, #id)")
    @Operation(summary = "Lister les consultations par assuré")
    public ResponseEntity<?> getByAssure(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.getConsultationsByAssure(id));
    }

    @GetMapping("/generaliste/{id}")
    @PreAuthorize("hasAnyRole('ORGANISME','MEDECIN') or @securityService.isSelfGeneraliste(principal, #id)")
    @Operation(summary = "Lister les consultations par généraliste — Réservé aux médecins généralistes (médecin traitant). Renvoie 400 si l'ID fourni correspond à un spécialiste.")
    public ResponseEntity<?> getByGeneraliste(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.getConsultationsByGeneraliste(id));
    }
}
