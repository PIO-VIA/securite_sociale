package com.enspy.csi.controller;

import com.enspy.csi.dto.response.MedecinResponseDTO;
import com.enspy.csi.repository.SpecialisteRepository;
import com.enspy.csi.service.MedecinService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class SpecialisteControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private MedecinService medecinService;

    @MockitoBean
    private SpecialisteRepository specialisteRepository;

    @MockitoBean
    private com.enspy.csi.repository.MedecinRepository medecinRepository;

    @MockitoBean
    private com.enspy.csi.repository.AssureRepository assureRepository;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void getAll_WithPatient_ShouldReturnOk() throws Exception {
        when(medecinService.getAllMedecins()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/specialistes")
                .with(user("patient").roles("ASSURE")))
                .andExpect(status().isOk());
    }

    @Test
    public void getById_WithAgent_ShouldReturnOk() throws Exception {
        when(medecinService.getMedecinById(1L)).thenReturn(new MedecinResponseDTO());

        mockMvc.perform(get("/api/specialistes/1")
                .with(user("agent").roles("ORGANISME")))
                .andExpect(status().isOk());
    }

    @Test
    public void getByDomaine_WithPatient_ShouldReturnOk() throws Exception {
        when(specialisteRepository.findByDomaineSpecialisation("CARDIOLOGIE")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/specialistes/domaine/CARDIOLOGIE")
                .with(user("patient").roles("ASSURE")))
                .andExpect(status().isOk());
    }

    @Test
    public void getAssuresBySpecialiste_WithAgent_ShouldReturnOk() throws Exception {
        com.enspy.csi.entity.Specialiste specialiste = new com.enspy.csi.entity.Specialiste();
        specialiste.setId(1L);
        specialiste.setMatricule("SPEC123");

        when(medecinRepository.findById(1L)).thenReturn(java.util.Optional.of(specialiste));
        when(assureRepository.findAssuresBySpecialisteMatricule("SPEC123")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/specialistes/1/assures")
                .with(user("agent").roles("ORGANISME")))
                .andExpect(status().isOk());
    }

    @Test
    public void getMyAssures_WithSpecialist_ShouldReturnOk() throws Exception {
        com.enspy.csi.entity.Specialiste specialiste = new com.enspy.csi.entity.Specialiste();
        specialiste.setId(1L);
        specialiste.setMatricule("SPEC123");
        specialiste.setEmail("specialist@example.com");

        when(medecinRepository.findByEmail("specialist@example.com")).thenReturn(java.util.Optional.of(specialiste));
        when(assureRepository.findAssuresBySpecialisteMatricule("SPEC123")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/specialistes/me/assures")
                .with(user("specialist@example.com").roles("MEDECIN")))
                .andExpect(status().isOk());
    }
}