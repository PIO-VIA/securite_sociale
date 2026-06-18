package com.enspy.csi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Expose le dossier de stockage des photos en tant que ressources statiques.
 * Les photos enregistrées sur le VPS (UPLOAD_DIR) sont ainsi accessibles via HTTP
 * à l'URL définie par app.upload.base-url (ex: /uploads/**).
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final FileStorageProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String base = properties.getBaseUrl();
        if (base == null || base.isBlank()) {
            base = "/uploads";
        }
        if (!base.startsWith("/")) {
            base = "/" + base;
        }
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        String emplacement = Paths.get(properties.getDir()).toAbsolutePath().normalize().toUri().toString();

        registry.addResourceHandler(base + "/**")
                .addResourceLocations(emplacement);
    }
}
