package com.enspy.csi.controller;

import com.enspy.csi.dto.request.PrescriptionRequestDTO;
import com.enspy.csi.dto.response.PrescriptionResponseDTO;
import com.enspy.csi.service.PrescriptionService;
import com.enspy.csi.service.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class PrescriptionControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private PrescriptionService prescriptionService;

    @MockitoBean(name = "securityService")
    private SecurityService securityService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void prescrireMedicament_WithDoctor_ShouldReturnOk() throws Exception {
        PrescriptionRequestDTO dto = new PrescriptionRequestDTO();
        when(prescriptionService.ajouterPrescriptionMedicament(any(PrescriptionRequestDTO.class))).thenReturn(new PrescriptionResponseDTO());

        mockMvc.perform(post("/api/prescriptions/medicament")
                .with(user("doctor").roles("MEDECIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void prescrireConsultation_WithDoctor_ShouldReturnOk() throws Exception {
        PrescriptionRequestDTO dto = new PrescriptionRequestDTO();
        when(prescriptionService.ajouterPrescriptionConsultation(any(PrescriptionRequestDTO.class))).thenReturn(new PrescriptionResponseDTO());

        mockMvc.perform(post("/api/prescriptions/consultation")
                .with(user("doctor").roles("MEDECIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void getByConsultation_WithParticipant_ShouldReturnOk() throws Exception {
        when(securityService.isConsultationParticipant(any(), eq(1L))).thenReturn(true);
        when(prescriptionService.getPrescriptionsByConsultation(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/prescriptions/consultation/1")
                .with(user("patient").roles("ASSURE")))
                .andExpect(status().isOk());
    }

    @Test
    public void modifier_WithDoctor_ShouldReturnOk() throws Exception {
        PrescriptionRequestDTO dto = new PrescriptionRequestDTO();
        when(prescriptionService.modifierPrescription(eq(1L), any(PrescriptionRequestDTO.class))).thenReturn(new PrescriptionResponseDTO());

        mockMvc.perform(put("/api/prescriptions/1")
                .with(user("doctor").roles("MEDECIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void supprimer_WithDoctor_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/prescriptions/1")
                .with(user("doctor").roles("MEDECIN")))
                .andExpect(status().isNoContent());
    }
}