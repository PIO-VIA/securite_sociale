package com.enspy.csi.controller;

import com.enspy.csi.dto.request.AssureRequestDTO;
import com.enspy.csi.dto.response.AssureResponseDTO;
import com.enspy.csi.service.AssureService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AssureControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private AssureService assureService;

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
    public void inscrire_WithAgent_ShouldReturnOk() throws Exception {
        AssureRequestDTO dto = new AssureRequestDTO();
        when(assureService.inscrireAssure(any(AssureRequestDTO.class))).thenReturn(new AssureResponseDTO());

        mockMvc.perform(post("/api/assures")
                .with(user("agent").roles("ORGANISME"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void getById_WithAgent_ShouldReturnOk() throws Exception {
        when(assureService.getAssureById(1L)).thenReturn(new AssureResponseDTO());

        mockMvc.perform(get("/api/assures/1")
                .with(user("agent").roles("ORGANISME")))
                .andExpect(status().isOk());
    }

    @Test
    public void getById_WithSelf_ShouldReturnOk() throws Exception {
        when(assureService.getAssureById(1L)).thenReturn(new AssureResponseDTO());
        when(securityService.isSelfAssure(any(), eq(1L))).thenReturn(true);

        mockMvc.perform(get("/api/assures/1")
                .with(user("patient").roles("ASSURE")))
                .andExpect(status().isOk());
    }

    @Test
    public void getAll_WithAgent_ShouldReturnOk() throws Exception {
        when(assureService.getAllAssures()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/assures")
                .with(user("agent").roles("ORGANISME")))
                .andExpect(status().isOk());
    }

    @Test
    public void update_WithAgent_ShouldReturnOk() throws Exception {
        AssureRequestDTO dto = new AssureRequestDTO();
        when(assureService.updateAssure(eq(1L), any(AssureRequestDTO.class))).thenReturn(new AssureResponseDTO());

        mockMvc.perform(put("/api/assures/1")
                .with(user("agent").roles("ORGANISME"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    public void delete_WithAgent_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/assures/1")
                .with(user("agent").roles("ORGANISME")))
                .andExpect(status().isNoContent());
    }

    @Test
    public void choisirMedecin_WithSelf_ShouldReturnOk() throws Exception {
        when(securityService.isSelfAssure(any(), eq(1L))).thenReturn(true);
        when(assureService.choisirMedecin(1L, 2L)).thenReturn(new AssureResponseDTO());

        mockMvc.perform(patch("/api/assures/1/choisir-medecin/2")
                .with(user("patient").roles("ASSURE")))
                .andExpect(status().isOk());
    }
}