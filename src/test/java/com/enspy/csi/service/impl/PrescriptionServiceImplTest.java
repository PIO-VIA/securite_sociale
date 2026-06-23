package com.enspy.csi.service.impl;

import com.enspy.csi.dto.response.PrescriptionResponseDTO;
import com.enspy.csi.entity.*;
import com.enspy.csi.exception.ResourceNotFoundException;
import com.enspy.csi.repository.ConsultationRepository;
import com.enspy.csi.repository.MedecinRepository;
import com.enspy.csi.repository.PrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceImplTest {

    @Mock
    private PrescriptionRepository prescriptionRepository;

    @Mock
    private ConsultationRepository consultationRepository;

    @Mock
    private MedecinRepository medecinRepository;

    @InjectMocks
    private PrescriptionServiceImpl prescriptionService;

    private PrescriptionConsultation prescription;
    private Consultation consultation;
    private Generaliste generaliste;
    private Assure assure;

    @BeforeEach
    void setUp() {
        generaliste = new Generaliste();
        generaliste.setId(1L);
        generaliste.setNom("Dr. Jean Dupont");
        generaliste.setMatricule("GEN-1122");

        assure = new Assure();
        assure.setId(8L);
        assure.setIdAssure("ASS-7766");
        assure.setNom("Marc Kengne");
        assure.setNumTelephone("677889900");
        assure.setSexe("M");
        assure.setGroupeSanguin("O+");

        consultation = new Consultation();
        consultation.setId(45L);
        consultation.setDate(LocalDate.of(2026, 6, 20));
        consultation.setGeneraliste(generaliste);
        consultation.setAssure(assure);

        prescription = new PrescriptionConsultation();
        prescription.setId(12L);
        prescription.setConsultation(consultation);
        prescription.setMatriculeMedecin("SPEC-9988");
        prescription.setMotif("Suspicion de cardiopathie");
    }

    @Test
    void getPrescriptionsForSpecialiste_ShouldReturnMappedDTOs() {
        when(prescriptionRepository.findConsultationsByMatriculeMedecin("SPEC-9988"))
                .thenReturn(List.of(prescription));

        List<PrescriptionResponseDTO> result = prescriptionService.getPrescriptionsForSpecialiste("SPEC-9988");

        assertNotNull(result);
        assertEquals(1, result.size());
        
        PrescriptionResponseDTO dto = result.get(0);
        assertEquals(12L, dto.getId());
        assertEquals(45L, dto.getConsultationId());
        assertEquals("CONSULTATION_SPECIALISTE", dto.getType());
        assertEquals("SPEC-9988", dto.getMatriculeMedecin());
        assertEquals("Suspicion de cardiopathie", dto.getMotif());
        assertEquals(LocalDate.of(2026, 6, 20), dto.getDateConsultation());
        assertEquals("Dr. Jean Dupont", dto.getMedecinPrescripteurNom());
        
        assertNotNull(dto.getAssure());
        assertEquals(8L, dto.getAssure().getId());
        assertEquals("ASS-7766", dto.getAssure().getIdAssure());
        assertEquals("Marc Kengne", dto.getAssure().getNom());
        assertEquals("677889900", dto.getAssure().getNumTelephone());
        assertEquals("M", dto.getAssure().getSexe());
        assertEquals("O+", dto.getAssure().getGroupeSanguin());
    }

    @Test
    void getPrescriptionsForSpecialisteEmail_ShouldReturnMappedDTOs() {
        Specialiste specialiste = new Specialiste();
        specialiste.setMatricule("SPEC-9988");
        specialiste.setEmail("specialist@example.com");

        when(medecinRepository.findByEmail("specialist@example.com")).thenReturn(Optional.of(specialiste));
        when(prescriptionRepository.findConsultationsByMatriculeMedecin("SPEC-9988"))
                .thenReturn(List.of(prescription));

        List<PrescriptionResponseDTO> result = prescriptionService.getPrescriptionsForSpecialisteEmail("specialist@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SPEC-9988", result.get(0).getMatriculeMedecin());
    }

    @Test
    void getPrescriptionsForSpecialisteEmail_WhenNotFound_ShouldThrowResourceNotFoundException() {
        when(medecinRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            prescriptionService.getPrescriptionsForSpecialisteEmail("unknown@example.com");
        });
    }
}
