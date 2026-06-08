package com.enspy.csi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void getAssures_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/assures"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getAssures_WithAgent_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/assures")
                .with(user("agent").roles("ORGANISME")))
                .andExpect(status().isOk());
    }

    @Test
    public void getAssures_WithPatient_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/assures")
                .with(user("patient").roles("ASSURE")))
                .andExpect(status().isForbidden());
    }
}
