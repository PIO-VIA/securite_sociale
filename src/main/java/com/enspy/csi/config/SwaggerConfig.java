package com.enspy.csi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API Sécurité Sociale")
                .version("1.0.0")
                .description("API REST pour la gestion des patients, médecins, consultations et remboursements")
                .contact(new Contact()
                    .name("PIO - ENSPY")
                    .email("csi@enspy.cm"))
                .license(new License()
                    .name("ENSPY License")
                    .url("https://enspy.cm")));
    }
}
