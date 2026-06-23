package com.enspy.csi.controller;

import com.enspy.csi.dto.request.PrescriptionRequestDTO;
import com.enspy.csi.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@Tag(name = "Prescriptions", description = "Gestion des prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping("/medicament")
    @PreAuthorize("hasRole('MEDECIN')")
    @Operation(summary = "Prescrire un médicament")
    public ResponseEntity<?> prescrireMedicament(@RequestBody PrescriptionRequestDTO dto) {
        return ResponseEntity.ok(prescriptionService.ajouterPrescriptionMedicament(dto));
    }

    @PostMapping("/consultation")
    @PreAuthorize("hasRole('MEDECIN')")
    @Operation(summary = "Prescrire une consultation chez un spécialiste")
    public ResponseEntity<?> prescrireConsultation(@RequestBody PrescriptionRequestDTO dto) {
        return ResponseEntity.ok(prescriptionService.ajouterPrescriptionConsultation(dto));
    }

    @GetMapping("/consultation/{consultationId}")
    @PreAuthorize("hasAnyRole('ORGANISME', 'MEDECIN') or @securityService.isConsultationParticipant(principal, #consultationId)")
    @Operation(summary = "Lister toutes les prescriptions d'une consultation")
    public ResponseEntity<?> getByConsultation(@PathVariable Long consultationId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByConsultation(consultationId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MEDECIN')")
    @Operation(summary = "Modifier une prescription")
    public ResponseEntity<?> modifier(@PathVariable Long id, @RequestBody PrescriptionRequestDTO dto) {
        return ResponseEntity.ok(prescriptionService.modifierPrescription(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MEDECIN')")
    @Operation(summary = "Supprimer une prescription")
    public ResponseEntity<?> supprimer(@PathVariable Long id) {
        prescriptionService.supprimerPrescription(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/specialiste/me")
    @PreAuthorize("hasRole('MEDECIN')")
    @Operation(summary = "Lister les orientations (prescriptions de consultation) adressées au spécialiste connecté")
    public ResponseEntity<?> getMesOrientations(Authentication authentication) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsForSpecialisteEmail(authentication.getName()));
    }

    @GetMapping("/specialiste/matricule/{matricule}")
    @PreAuthorize("hasRole('ORGANISME') or (hasRole('MEDECIN') and @securityService.isMedecinMatricule(principal, #matricule))")
    @Operation(summary = "Lister les orientations (prescriptions de consultation) adressées à un spécialiste par son matricule")
    public ResponseEntity<?> getOrientationsByMatricule(@PathVariable String matricule) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsForSpecialiste(matricule));
    }
}
