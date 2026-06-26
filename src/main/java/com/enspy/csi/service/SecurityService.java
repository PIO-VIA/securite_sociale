package com.enspy.csi.service;

import com.enspy.csi.entity.*;
import com.enspy.csi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final AssureRepository assureRepository;
    private final GeneralisteRepository generalisteRepository;
    private final ConsultationRepository consultationRepository;
    private final FeuillemMaladieRepository feuillemMaladieRepository;
    private final RemboursementRepository remboursementRepository;
    private final MedecinRepository medecinRepository;

    public boolean isMedecinMatricule(Object principal, String matricule) {
        if (!(principal instanceof UserDetails userDetails)) {
            return false;
        }
        String username = userDetails.getUsername(); // email
        return medecinRepository.findByEmail(username)
                .map(m -> m.getMatricule().equals(matricule))
                .orElse(false);
    }

    public boolean isSelfAssure(Object principal, Long assureId) {
        if (!(principal instanceof UserDetails userDetails)) {
            return false;
        }
        String username = userDetails.getUsername();
        Optional<Assure> assure = assureRepository.findById(assureId);
        return assure.map(value -> username.equals(value.getIdAssure())).orElse(false);
    }

    public boolean isSelfGeneraliste(Object principal, Long generalisteId) {
        if (!(principal instanceof UserDetails userDetails)) {
            return false;
        }
        String username = userDetails.getUsername();
        Optional<Medecin> gen = medecinRepository.findById(generalisteId);
        return gen.map(value -> username.equals(value.getEmail())).orElse(false);
    }

    public boolean isConsultationParticipant(Object principal, Long consultationId) {
        if (!(principal instanceof UserDetails userDetails)) {
            return false;
        }
        String username = userDetails.getUsername();
        Optional<Consultation> consOpt = consultationRepository.findById(consultationId);
        if (consOpt.isEmpty()) {
            return false;
        }
        Consultation c = consOpt.get();
        if (c.getGeneraliste() != null && username.equals(c.getGeneraliste().getMatricule())) {
            return true;
        }
        if (c.getAssure() != null && username.equals(c.getAssure().getIdAssure())) {
            return true;
        }
        return false;
    }

    public boolean isMedecinOfFeuille(Object principal, Long feuilleId) {
        if (!(principal instanceof UserDetails userDetails)) {
            return false;
        }
        String username = userDetails.getUsername();
        Optional<FeuillemMaladie> fmOpt = feuillemMaladieRepository.findById(feuilleId);
        if (fmOpt.isEmpty()) {
            return false;
        }
        FeuillemMaladie fm = fmOpt.get();
        if (fm.getConsultation() != null && fm.getConsultation().getGeneraliste() != null) {
            return username.equals(fm.getConsultation().getGeneraliste().getEmail());
        }
        return false;
    }

    public boolean isSelfAssureForFeuille(Object principal, Long feuilleId) {
        if (!(principal instanceof UserDetails userDetails)) {
            return false;
        }
        String username = userDetails.getUsername();
        Optional<FeuillemMaladie> fmOpt = feuillemMaladieRepository.findById(feuilleId);
        if (fmOpt.isEmpty()) {
            return false;
        }
        FeuillemMaladie fm = fmOpt.get();
        if (fm.getConsultation() != null && fm.getConsultation().getAssure() != null) {
            return username.equals(fm.getConsultation().getAssure().getIdAssure());
        }
        return false;
    }

    public boolean isSelfAssureForRemboursement(Object principal, Long remboursementId) {
        if (!(principal instanceof UserDetails userDetails)) {
            return false;
        }
        String username = userDetails.getUsername();
        Optional<Remboursement> rOpt = remboursementRepository.findById(remboursementId);
        if (rOpt.isEmpty()) {
            return false;
        }
        Remboursement r = rOpt.get();
        if (r.getFeuilleMaladie() != null 
                && r.getFeuilleMaladie().getConsultation() != null 
                && r.getFeuilleMaladie().getConsultation().getAssure() != null) {
            return username.equals(r.getFeuilleMaladie().getConsultation().getAssure().getIdAssure());
        }
        return false;
    }
}
