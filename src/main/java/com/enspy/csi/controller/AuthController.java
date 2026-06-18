package com.enspy.csi.controller;

import com.enspy.csi.dto.request.AgentRequestDTO;
import com.enspy.csi.dto.request.ChangePasswordRequestDTO;
import com.enspy.csi.dto.request.LoginRequestDTO;
import com.enspy.csi.dto.response.AuthResponseDTO;
import com.enspy.csi.service.AgentService;
import com.enspy.csi.service.AssureService;
import com.enspy.csi.service.MedecinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints de connexion et d'inscription")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final MedecinService medecinService;
    private final AgentService agentService;
    private final AssureService assureService;
    private final com.enspy.csi.config.JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "Connexion pour les médecins et l'organisme")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse("ROLE_USER");

            if (!"ROLE_ORGANISME".equals(role) && !"ROLE_MEDECIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Rôle non autorisé pour cette interface. Seuls les médecins et l'organisme peuvent se connecter ici.");
            }

            String token = jwtUtil.generateToken(dto.getEmail(), role);

            return ResponseEntity.ok(AuthResponseDTO.builder()
                    .username(dto.getEmail())
                    .role(role)
                    .token(token)
                    .message("Connexion réussie")
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Identifiants incorrects : " + e.getMessage());
        }
    }

    @PatchMapping("/change-password")
    @PreAuthorize("hasRole('MEDECIN')")
    @Operation(summary = "Modifier son mot de passe (médecin connecté)")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO dto, Authentication authentication) {
        medecinService.changerMotDePasse(authentication.getName(), dto);
        return ResponseEntity.ok("Mot de passe modifié avec succès.");
    }

    @PostMapping("/register-organisme")
    @Operation(summary = "Inscription d'un nouvel agent de l'organisme")
    public ResponseEntity<?> registerOrganisme(@RequestBody AgentRequestDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty() ||
            dto.getMotDePasse() == null || dto.getMotDePasse().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("L'email et le mot de passe sont obligatoires.");
        }

        try {
            return ResponseEntity.ok(agentService.creerAgent(dto));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement de l'agent : " + e.getMessage());
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Récupérer le profil de l'utilisateur connecté")
    public ResponseEntity<?> me(Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        try {
            return switch (role) {
                case "ROLE_ORGANISME" -> ResponseEntity.ok(agentService.getAgentByEmail(username));
                case "ROLE_MEDECIN" -> ResponseEntity.ok(medecinService.getMedecinByEmail(username));
                case "ROLE_ASSURE" -> ResponseEntity.ok(assureService.getAssureByEmail(username));
                default -> ResponseEntity.ok(AuthResponseDTO.builder()
                        .username(username)
                        .role(role)
                        .message("Profil non détaillé pour ce compte.")
                        .build());
            };
        } catch (Exception e) {
            // Compte par défaut (agent/admin) sans entité associée
            return ResponseEntity.ok(AuthResponseDTO.builder()
                    .username(username)
                    .role(role)
                    .message("Profil non détaillé pour ce compte.")
                    .build());
        }
    }
}
