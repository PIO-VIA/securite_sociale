package com.enspy.csi.controller;

import com.enspy.csi.dto.response.MedecinResponseDTO;
import com.enspy.csi.repository.AssureRepository;
import com.enspy.csi.service.MedecinService;
import com.enspy.csi.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class GeneralisteControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private MedecinService medecinService;

    @MockitoBean
    private AssureRepository assureRepository;

    @MockitoBean(name = "securityService")
    private SecurityService securityService;

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

        mockMvc.perform(get("/api/generalistes")
                .with(user("patient").roles("ASSURE")))
                .andExpect(status().isOk());
    }

    @Test
    public void getById_WithDoctor_ShouldReturnOk() throws Exception {
        when(medecinService.getMedecinById(1L)).thenReturn(new MedecinResponseDTO());

        mockMvc.perform(get("/api/generalistes/1")
                .with(user("doctor").roles("MEDECIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void getAssuresByGeneraliste_WithSelfDoctor_ShouldReturnOk() throws Exception {
        when(securityService.isSelfGeneraliste(any(), eq(1L))).thenReturn(true);
        when(assureRepository.findByMedecinTraitantId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/generalistes/1/assures")
                .with(user("doctor").roles("MEDECIN")))
                .andExpect(status().isOk());
    }
}