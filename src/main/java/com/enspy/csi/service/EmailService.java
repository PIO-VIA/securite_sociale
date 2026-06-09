package com.enspy.csi.service;

public interface EmailService {
    void envoyerMotDePasseMedecin(String destinataire, String nom, String motDePasse);
}
