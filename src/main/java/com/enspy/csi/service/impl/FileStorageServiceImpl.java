package com.enspy.csi.service.impl;

import com.enspy.csi.config.FileStorageProperties;
import com.enspy.csi.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageProperties properties;

    private static final List<String> EXTENSIONS_AUTORISEES = List.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long TAILLE_MAX = 5L * 1024 * 1024; // 5 Mo

    @Override
    public String stockerImage(MultipartFile file, String sousDossier) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        if (file.getSize() > TAILLE_MAX) {
            throw new IllegalArgumentException("La photo ne doit pas dépasser 5 Mo.");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(original);
        if (extension == null || !EXTENSIONS_AUTORISEES.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Format de photo non supporté. Formats acceptés : " + String.join(", ", EXTENSIONS_AUTORISEES));
        }

        try {
            Path dossierCible = Paths.get(properties.getDir(), sousDossier).toAbsolutePath().normalize();
            Files.createDirectories(dossierCible);

            String nomFichier = UUID.randomUUID().toString().replace("-", "") + "." + extension.toLowerCase();
            Path cible = dossierCible.resolve(nomFichier);

            try (var in = file.getInputStream()) {
                Files.copy(in, cible, StandardCopyOption.REPLACE_EXISTING);
            }

            // URL publique relative (ex: /uploads/assures/uuid.png)
            return normaliserBaseUrl() + "/" + sousDossier + "/" + nomFichier;
        } catch (IOException e) {
            throw new IllegalStateException("Échec de l'enregistrement de la photo : " + e.getMessage(), e);
        }
    }

    @Override
    public void supprimer(String urlPublique) {
        if (urlPublique == null || urlPublique.isBlank()) {
            return;
        }
        String prefix = normaliserBaseUrl();
        if (!urlPublique.startsWith(prefix)) {
            return;
        }
        String relatif = urlPublique.substring(prefix.length()).replaceFirst("^/+", "");
        try {
            Path chemin = Paths.get(properties.getDir(), relatif).toAbsolutePath().normalize();
            Files.deleteIfExists(chemin);
        } catch (IOException ignored) {
            // best-effort : on ne bloque pas l'opération métier si la suppression échoue
        }
    }

    private String normaliserBaseUrl() {
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
        return base;
    }
}
