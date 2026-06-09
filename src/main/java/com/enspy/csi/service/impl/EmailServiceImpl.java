package com.enspy.csi.service.impl;

import com.enspy.csi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:csi@enspy.cm}")
    private String fromAddress;

    @Override
    public void envoyerMotDePasseMedecin(String destinataire, String nom, String motDePasse) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(destinataire);
        message.setSubject("CSI - Vos identifiants de connexion");
        message.setText(String.format(
                "Bonjour Dr %s,%n%n" +
                "Votre compte médecin a été créé sur la plateforme CSI.%n%n" +
                "Identifiant : %s%n" +
                "Mot de passe temporaire : %s%n%n" +
                "Connectez-vous et modifiez votre mot de passe depuis votre espace personnel.%n%n" +
                "Cordialement,%nL'équipe CSI",
                nom, destinataire, motDePasse
        ));
        mailSender.send(message);
    }
}
