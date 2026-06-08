package com.enspy.csi.controller;

import com.enspy.csi.dto.request.MedecinRequestDTO;
import com.enspy.csi.dto.response.MedecinResponseDTO;
import com.enspy.csi.service.MedecinService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class MedecinControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private MedecinService medecinService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void enregistrer_WithAgent_ShouldReturnOk() throws Exception {
        MedecinRequestDTO dto = new MedecinRequestDTO();
        when(medecinService.enregistrerMedecin(any(MedecinRequestDTO.class))).thenReturn(new MedecinResponseDTO());

        mockMvc.perform(post("/api/medecins")
                .with(user("agent").roles("ORGANISME"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void getById_WithPatient_ShouldReturnOk() throws Exception {
        when(medecinService.getMedecinById(1L)).thenReturn(new MedecinResponseDTO());

        mockMvc.perform(get("/api/medecins/1")
                .with(user("patient").roles("ASSURE")))
                .andExpect(status().isOk());
    }

    @Test
    public void getAll_WithDoctor_ShouldReturnOk() throws Exception {
        when(medecinService.getAllMedecins()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/medecins")
                .with(user("doctor").roles("MEDECIN")))
                .andExpect(status().isOk());
    }
}