package com.enspy.csi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propriétés de configuration du stockage des fichiers (photos de profil).
 * Sur le VPS, il suffit de définir la variable d'environnement UPLOAD_DIR
 * (ex: /var/www/csi/uploads) pour que les photos soient stockées dans ce dossier.
 */
@Component
@ConfigurationProperties(prefix = "app.upload")
@Getter
@Setter
public class FileStorageProperties {

    /** Dossier physique où les fichiers sont stockés (absolu de préférence sur le VPS). */
    private String dir = "./uploads";

    /** Préfixe d'URL public exposant les fichiers (ex: /uploads). */
    private String baseUrl = "/uploads";
}
