package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.MedecinRequestDTO;
import com.enspy.csi.dto.response.MedecinResponseDTO;
import com.enspy.csi.entity.Generaliste;
import com.enspy.csi.entity.Specialiste;
import com.enspy.csi.repository.GeneralisteRepository;
import com.enspy.csi.repository.MedecinRepository;
import com.enspy.csi.repository.SpecialisteRepository;
import com.enspy.csi.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedecinServiceImplTest {

    @Mock
    private MedecinRepository medecinRepository;

    @Mock
    private GeneralisteRepository generalisteRepository;

    @Mock
    private SpecialisteRepository specialisteRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MedecinServiceImpl medecinService;

    private MedecinRequestDTO dto;

    @BeforeEach
    void setUp() {
        dto = new MedecinRequestDTO();
        dto.setNom("Jean Dupont");
        dto.setEmail("jean.dupont@example.com");
        dto.setMatricule("MED123");
        dto.setSexe("M");
        dto.setDateNaissance(LocalDate.of(1980, 5, 12));
        dto.setIndicatifPays("+237");
        dto.setNumTelephone("677777777");
        dto.setEstAssure(false);
    }

    @Test
    void enregistrerMedecin_Generaliste_ShouldSendEmail() {
        dto.setType("GENERALISTE");

        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        when(generalisteRepository.save(any(Generaliste.class))).thenAnswer(invocation -> {
            Generaliste g = invocation.getArgument(0);
            g.setId(1L);
            return g;
        });

        MedecinResponseDTO response = medecinService.enregistrerMedecin(dto);

        assertNotNull(response);
        assertEquals("GENERALISTE", response.getType());
        assertEquals("jean.dupont@example.com", response.getEmail());

        // Verify that the email service was called to send the temporary password
        verify(emailService, times(1)).envoyerMotDePasseMedecin(
                eq("jean.dupont@example.com"),
                eq("Jean Dupont"),
                anyString()
        );
    }

    @Test
    void enregistrerMedecin_Specialiste_ShouldSendEmail() {
        dto.setType("SPECIALISTE");
        dto.setDomaineSpecialisation("Cardiologie");

        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        when(specialisteRepository.save(any(Specialiste.class))).thenAnswer(invocation -> {
            Specialiste s = invocation.getArgument(0);
            s.setId(2L);
            return s;
        });

        MedecinResponseDTO response = medecinService.enregistrerMedecin(dto);

        assertNotNull(response);
        assertEquals("SPECIALISTE", response.getType());
        assertEquals("jean.dupont@example.com", response.getEmail());
        assertEquals("Cardiologie", response.getDomaineSpecialisation());

        // Verify that the email service was called to send the temporary password
        verify(emailService, times(1)).envoyerMotDePasseMedecin(
                eq("jean.dupont@example.com"),
                eq("Jean Dupont"),
                anyString()
        );
    }

    @Test
    void enregistrerMedecin_WhenEmailSendingFails_ShouldThrowIllegalStateException() {
        dto.setType("GENERALISTE");

        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        when(generalisteRepository.save(any(Generaliste.class))).thenAnswer(invocation -> {
            Generaliste g = invocation.getArgument(0);
            g.setId(1L);
            return g;
        });

        // Simulate mail sending failure
        doThrow(new RuntimeException("Mail server down"))
                .when(emailService).envoyerMotDePasseMedecin(anyString(), anyString(), anyString());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            medecinService.enregistrerMedecin(dto);
        });

        assertTrue(exception.getMessage().contains("l'envoi de l'email a échoué"));
    }

    @Test
    void enregistrerMedecin_WithoutEmail_ShouldThrowIllegalArgumentException() {
        dto.setEmail(null);
        dto.setType("GENERALISTE");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            medecinService.enregistrerMedecin(dto);
        });

        assertEquals("L'email est obligatoire pour créer un médecin.", exception.getMessage());
        verifyNoInteractions(emailService);
        verifyNoInteractions(generalisteRepository);
    }
}
