package com.enspy.csi.config;

import com.enspy.csi.entity.Assure;
import com.enspy.csi.entity.Medecin;
import com.enspy.csi.repository.AssureRepository;
import com.enspy.csi.repository.MedecinRepository;
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

    public void registerOrganisme(String username, String rawPassword) {
        registeredOrganismes.put(username.toLowerCase(), passwordEncoder.encode(rawPassword));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Organisme agent
        if ("agent".equalsIgnoreCase(username) || "admin".equalsIgnoreCase(username)) {
            return User.withUsername(username)
                    .password(passwordEncoder.encode("password"))
                    .roles("ORGANISME")
                    .build();
        }

        // 1b. Registered Organisme
        if (registeredOrganismes.containsKey(username.toLowerCase())) {
            return User.withUsername(username)
                    .password(registeredOrganismes.get(username.toLowerCase()))
                    .roles("ORGANISME")
                    .build();
        }

        // 2. Doctor by matricule
        Optional<Medecin> medecinOpt = medecinRepository.findByMatricule(username);
        if (medecinOpt.isPresent()) {
            return User.withUsername(username)
                    .password(passwordEncoder.encode("password"))
                    .roles("MEDECIN")
                    .build();
        }

        // 3. Patient by idAssure
        Optional<Assure> assureOpt = assureRepository.findByIdAssure(username);
        if (assureOpt.isPresent()) {
            return User.withUsername(username)
                    .password(passwordEncoder.encode("password"))
                    .roles("ASSURE")
                    .build();
        }

        throw new UsernameNotFoundException("Utilisateur non trouvé: " + username);
    }
}
