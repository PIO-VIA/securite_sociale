package com.enspy.csi.controller;

import com.enspy.csi.dto.request.PrescriptionRequestDTO;
import com.enspy.csi.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ORGANISME','MEDECIN') or @securityService.isConsultationParticipant(principal, #consultationId)")
    @Operation(summary = "Lister toutes les prescriptions d'une consultation")
    public ResponseEntity<?> getByConsultation(@PathVariable Long consultationId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByConsultation(consultationId));
    }
}
