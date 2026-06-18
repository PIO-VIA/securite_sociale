package com.enspy.csi.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Sauvegarde un fichier image dans un sous-dossier et retourne l'URL publique relative.
     *
     * @param file      le fichier envoyé (peut être null -> retourne null)
     * @param sousDossier sous-dossier de classement (ex: "assures", "medecins", "agents")
     * @return l'URL publique (ex: /uploads/assures/uuid.png) ou null si aucun fichier
     */
    String stockerImage(MultipartFile file, String sousDossier);

    /**
     * Supprime un fichier à partir de son URL publique (best-effort, ne lève pas si absent).
     */
    void supprimer(String urlPublique);
}
