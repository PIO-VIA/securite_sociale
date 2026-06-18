package com.enspy.csi.controller;

import com.enspy.csi.dto.request.AgentRequestDTO;
import com.enspy.csi.dto.request.LoginRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void registerAndLoginOrganisme_ShouldSucceed() throws Exception {
        // 1. Register a new organism agent
        AgentRequestDTO registerDto = new AgentRequestDTO();
        registerDto.setEmail("new_agent@organisme.com");
        registerDto.setMotDePasse("secure_pass");
        registerDto.setNom("Nouvel Agent");
        mockMvc.perform(post("/api/auth/register-organisme")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk());

        // 2. Login with the registered agent
        LoginRequestDTO loginDto = new LoginRequestDTO("new_agent@organisme.com", "secure_pass");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("new_agent@organisme.com"))
                .andExpect(jsonPath("$.role").value("ROLE_ORGANISME"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void loginStaticAgent_ShouldSucceed() throws Exception {
        LoginRequestDTO loginDto = new LoginRequestDTO("agent", "password");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("agent"))
                .andExpect(jsonPath("$.role").value("ROLE_ORGANISME"));
    }

    @Test
    public void loginInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        LoginRequestDTO loginDto = new LoginRequestDTO("invalid_user", "bad_pass");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }
}
