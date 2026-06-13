package com.enspy.csi.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        // Set the value of fromAddress that would normally be injected by @Value
        ReflectionTestUtils.setField(emailService, "fromAddress", "csi@enspy.cm");
    }

    @Test
    void envoyerMotDePasseMedecin_ShouldConstructAndSendCorrectMail() {
        String destinataire = "test.medecin@example.com";
        String nom = "Dupont";
        String motDePasse = "TempPwd123!";

        emailService.envoyerMotDePasseMedecin(destinataire, nom, motDePasse);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
        assertEquals("csi@enspy.cm", sentMessage.getFrom());
        assertArrayEquals(new String[]{destinataire}, sentMessage.getTo());
        assertEquals("CSI - Vos identifiants de connexion", sentMessage.getSubject());
        
        String text = sentMessage.getText();
        assertNotNull(text);
        assertTrue(text.contains("Bonjour Dr Dupont,"));
        assertTrue(text.contains("Identifiant : test.medecin@example.com"));
        assertTrue(text.contains("Mot de passe temporaire : TempPwd123!"));
        assertTrue(text.contains("Cordialement,") && text.contains("L'équipe CSI"));
    }
}
