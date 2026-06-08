package com.enspy.csi.controller;

import com.enspy.csi.dto.response.RemboursementResponseDTO;
import com.enspy.csi.service.FeuillemMaladieService;
import com.enspy.csi.service.RemboursementService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class RemboursementControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private RemboursementService remboursementService;

    @MockitoBean
    private FeuillemMaladieService feuillemMaladieService;

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
    public void effectuerRemboursement_WithAgent_ShouldReturnOk() throws Exception {
        when(remboursementService.effectuerRemboursement(1L, "CASH")).thenReturn(new RemboursementResponseDTO());

        mockMvc.perform(post("/api/remboursements/1")
                .param("modePaiement", "CASH")
                .with(user("agent").roles("ORGANISME")))
                .andExpect(status().isOk());
    }

    @Test
    public void getById_WithSelf_ShouldReturnOk() throws Exception {
        when(securityService.isSelfAssureForRemboursement(any(), eq(1L))).thenReturn(true);
        when(remboursementService.getRemboursementById(1L)).thenReturn(new RemboursementResponseDTO());

        mockMvc.perform(get("/api/remboursements/1")
                .with(user("patient").roles("ASSURE")))
                .andExpect(status().isOk());
    }

    @Test
    public void getTotal_WithAgent_ShouldReturnOk() throws Exception {
        when(remboursementService.getTotalRemboursements()).thenReturn(1500.0);

        mockMvc.perform(get("/api/remboursements/stats/total")
                .with(user("agent").roles("ORGANISME")))
                .andExpect(status().isOk());
    }

    @Test
    public void getNonRembourses_WithAgent_ShouldReturnOk() throws Exception {
        when(feuillemMaladieService.getFeuillesNonRemboursees()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/remboursements/non-rembourses")
                .with(user("agent").roles("ORGANISME")))
                .andExpect(status().isOk());
    }
}