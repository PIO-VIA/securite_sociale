package com.enspy.csi.config;

import com.enspy.csi.entity.Assure;
import com.enspy.csi.entity.Medecin;
import com.enspy.csi.repository.AssureRepository;
import com.enspy.csi.repository.MedecinRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AssureRepository assureRepository;
    private final MedecinRepository medecinRepository;
    private final @Lazy PasswordEncoder passwordEncoder;

    private final java.util.concurrent.ConcurrentHashMap<String, String> registeredOrganismes = new java.util.concurrent.ConcurrentHashMap<>();

    // Hash calculé une seule fois au démarrage — évite le recalcul à chaque appel
    private String defaultEncodedPassword;

    @PostConstruct
    public void init() {
        defaultEncodedPassword = passwordEncoder.encode("password");
    }

    public void registerOrganisme(String username, String rawPassword) {
        registeredOrganismes.put(username.toLowerCase(), passwordEncoder.encode(rawPassword));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Organisme agent par défaut
        if ("agent".equalsIgnoreCase(username) || "admin".equalsIgnoreCase(username)) {
            return User.withUsername(username)
                    .password(defaultEncodedPassword)
                    .roles("ORGANISME")
                    .build();
        }

        // 1b. Organisme enregistré dynamiquement
        if (registeredOrganismes.containsKey(username.toLowerCase())) {
            return User.withUsername(username)
                    .password(registeredOrganismes.get(username.toLowerCase()))
                    .roles("ORGANISME")
                    .build();
        }

        // 2. Medecin par email uniquement
        Optional<Medecin> medecinOpt = medecinRepository.findByEmail(username);
        if (medecinOpt.isPresent()) {
            Medecin medecin = medecinOpt.get();
            String pwd = medecin.getMotDePasse() != null ? medecin.getMotDePasse() : defaultEncodedPassword;
            return User.withUsername(medecin.getEmail())
                    .password(pwd)
                    .roles("MEDECIN")
                    .build();
        }

        // 3. Assure par email uniquement
        Optional<Assure> assureOpt = assureRepository.findByEmail(username);
        if (assureOpt.isPresent()) {
            Assure assure = assureOpt.get();
            String pwd = assure.getMotDePasse() != null ? assure.getMotDePasse() : defaultEncodedPassword;
            return User.withUsername(assure.getEmail())
                    .password(pwd)
                    .roles("ASSURE")
                    .build();
        }

        throw new UsernameNotFoundException("Utilisateur non trouvé: " + username);
    }
}
