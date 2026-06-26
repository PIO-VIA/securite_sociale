package com.enspy.csi.controller;

import com.enspy.csi.dto.request.ConsultationRequestDTO;
import com.enspy.csi.dto.response.ConsultationResponseDTO;
import com.enspy.csi.service.ConsultationService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ConsultationControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private ConsultationService consultationService;

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
    public void creer_WithDoctor_ShouldReturnOk() throws Exception {
        ConsultationRequestDTO dto = new ConsultationRequestDTO();
        when(consultationService.creerConsultation(any(ConsultationRequestDTO.class))).thenReturn(new ConsultationResponseDTO());

        mockMvc.perform(post("/api/consultations")
                .with(user("doctor").roles("MEDECIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void getBySpecialiste_WithSelfDoctor_ShouldReturnOk() throws Exception {
        when(securityService.isSelfGeneraliste(any(), eq(2L))).thenReturn(true);
        when(consultationService.getConsultationsBySpecialiste(2L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/consultations/specialiste/2")
                .with(user("doctor").roles("MEDECIN")))
                .andExpect(status().isOk());
    }

   /* @Test
    public void getById_WithParticipant_ShouldReturnOk() throws Exception {
        when(securityService.isConsultationParticipant(any(), eq(1L))).thenReturn(true);
        when(consultationService.getConsultationById(1L)).thenReturn(new ConsultationResponseDTO());

        mockMvc.perform(get("/api/consultations/1")
                .with(user("patient").roles("ASSURE")))
                .andExpect(status().isOk());
    }

  @Test
    public void getByAssure_WithSelf_ShouldReturnOk() throws Exception {
        when(securityService.isSelfAssure(any(), eq(1L))).thenReturn(true);
        when(consultationService.getConsultationsByAssure(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/consultations/assure/1")
                .with(user("patient").roles("ASSURE")))
                .andExpect(status().isOk());
    }

    @Test
    public void getByGeneraliste_WithSelfDoctor_ShouldReturnOk() throws Exception {
        when(securityService.isSelfGeneraliste(any(), eq(2L))).thenReturn(true);
        when(consultationService.getConsultationsByGeneraliste(2L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/consultations/generaliste/2")
                .with(user("doctor").roles("MEDECIN")))
                .andExpect(status().isOk());
    }*/
}