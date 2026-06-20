package com.enspy.csi.controller;

import com.enspy.csi.dto.request.FeuillemMaladieRequestDTO;
import com.enspy.csi.dto.response.FeuillemMaladieResponseDTO;
import com.enspy.csi.service.FeuillemMaladieService;
import com.enspy.csi.service.SecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class FeuillemMaladieControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private FeuillemMaladieService feuillemMaladieService;

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
    public void enregistrer_WithDoctor_ShouldReturnOk() throws Exception {
        FeuillemMaladieRequestDTO dto = new FeuillemMaladieRequestDTO();
        when(feuillemMaladieService.enregistrerFeuilleMaladie(any(FeuillemMaladieRequestDTO.class))).thenReturn(new FeuillemMaladieResponseDTO());

        mockMvc.perform(post("/api/feuilles-maladie")
                .with(user("doctor").roles("MEDECIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void getById_WithSelf_ShouldReturnOk() throws Exception {
        when(securityService.isSelfAssureForFeuille(any(), eq(1L))).thenReturn(true);
        when(feuillemMaladieService.getFeuilleMaladieById(1L)).thenReturn(new FeuillemMaladieResponseDTO());

        mockMvc.perform(get("/api/feuilles-maladie/1")
                .with(user("patient").roles("ASSURE")))
                .andExpect(status().isOk());
    }

    @Test
    public void getAll_WithAgent_ShouldReturnOk() throws Exception {
        when(feuillemMaladieService.getAllFeuillesMaladie()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/feuilles-maladie")
                .with(user("agent").roles("ORGANISME")))
                .andExpect(status().isOk());
    }

    @Test
    public void getByAssure_WithSelf_ShouldReturnOk() throws Exception {
        when(securityService.isSelfAssure(any(), eq(1L))).thenReturn(true);
        when(feuillemMaladieService.getFeuillesByAssure(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/feuilles-maladie/assure/1")
                .with(user("patient").roles("ASSURE")))
                .andExpect(status().isOk());
    }

    @Test
    public void update_WithDoctor_ShouldReturnOk() throws Exception {
        FeuillemMaladieRequestDTO dto = new FeuillemMaladieRequestDTO();
        when(feuillemMaladieService.modifierFeuilleMaladie(eq(1L), any(FeuillemMaladieRequestDTO.class)))
                .thenReturn(new FeuillemMaladieResponseDTO());

        mockMvc.perform(put("/api/feuilles-maladie/1")
                .with(user("doctor").roles("MEDECIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void delete_WithAgent_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/feuilles-maladie/1")
                .with(user("agent").roles("ORGANISME")))
                .andExpect(status().isNoContent());
    }

    @Test
    public void annuler_WithAgent_ShouldReturnOk() throws Exception {
        when(feuillemMaladieService.annulerFeuilleMaladie(1L)).thenReturn(new FeuillemMaladieResponseDTO());

        mockMvc.perform(patch("/api/feuilles-maladie/1/annuler")
                .with(user("agent").roles("ORGANISME")))
                .andExpect(status().isOk());
    }
}