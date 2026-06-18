package com.enspy.csi.controller;

import com.enspy.csi.dto.request.AgentRequestDTO;
import com.enspy.csi.service.AgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
@Tag(name = "Agents", description = "Gestion des agents de l'organisme")
public class AgentController {

    private final AgentService agentService;

    @PostMapping
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Créer un nouvel agent de l'organisme")
    public ResponseEntity<?> creer(@RequestBody AgentRequestDTO dto) {
        return ResponseEntity.ok(agentService.creerAgent(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Récupérer un agent par ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(agentService.getAgentById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Lister tous les agents")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(agentService.getAllAgents());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Modifier un agent")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AgentRequestDTO dto) {
        return ResponseEntity.ok(agentService.updateAgent(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Supprimer un agent")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        agentService.deleteAgent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ORGANISME')")
    @Operation(summary = "Téléverser/mettre à jour la photo de profil d'un agent (optionnel)")
    public ResponseEntity<?> uploadPhoto(@PathVariable Long id, @RequestParam("photo") MultipartFile photo) {
        return ResponseEntity.ok(agentService.uploadPhoto(id, photo));
    }
}
