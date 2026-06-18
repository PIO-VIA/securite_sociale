package com.enspy.csi.service.impl;

import com.enspy.csi.dto.request.AgentRequestDTO;
import com.enspy.csi.dto.response.AgentResponseDTO;
import com.enspy.csi.entity.Agent;
import com.enspy.csi.exception.ResourceNotFoundException;
import com.enspy.csi.repository.AgentRepository;
import com.enspy.csi.service.AgentService;
import com.enspy.csi.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final FileStorageService fileStorageService;
    private final @org.springframework.context.annotation.Lazy org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public AgentResponseDTO creerAgent(AgentRequestDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("L'email est obligatoire pour créer un agent.");
        }
        if (agentRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("Un agent existe déjà avec cet email : " + dto.getEmail());
        }
        Agent agent = new Agent();
        appliquerChamps(agent, dto);
        if (dto.getMotDePasse() != null && !dto.getMotDePasse().isBlank()) {
            agent.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        }
        return toDTO(agentRepository.save(agent));
    }

    @Override
    public AgentResponseDTO getAgentById(Long id) {
        return toDTO(trouver(id));
    }

    @Override
    public AgentResponseDTO getAgentByEmail(String email) {
        Agent agent = agentRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable avec l'email : " + email));
        return toDTO(agent);
    }

    @Override
    public List<AgentResponseDTO> getAllAgents() {
        return agentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public AgentResponseDTO updateAgent(Long id, AgentRequestDTO dto) {
        Agent agent = trouver(id);
        appliquerChamps(agent, dto);
        if (dto.getMotDePasse() != null && !dto.getMotDePasse().isBlank()) {
            agent.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse()));
        }
        return toDTO(agentRepository.save(agent));
    }

    @Override
    public void deleteAgent(Long id) {
        Agent agent = trouver(id);
        if (agent.getPhotoUrl() != null) {
            fileStorageService.supprimer(agent.getPhotoUrl());
        }
        agentRepository.delete(agent);
    }

    @Override
    public AgentResponseDTO uploadPhoto(Long id, MultipartFile photo) {
        Agent agent = trouver(id);
        String url = fileStorageService.stockerImage(photo, "agents");
        if (url != null) {
            if (agent.getPhotoUrl() != null) {
                fileStorageService.supprimer(agent.getPhotoUrl());
            }
            agent.setPhotoUrl(url);
            agentRepository.save(agent);
        }
        return toDTO(agent);
    }

    private Agent trouver(Long id) {
        return agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent introuvable avec l'id : " + id));
    }

    private void appliquerChamps(Agent agent, AgentRequestDTO dto) {
        if (dto.getNom() != null) agent.setNom(dto.getNom());
        if (dto.getDateNaissance() != null) agent.setDateNaissance(dto.getDateNaissance());
        if (dto.getSexe() != null) agent.setSexe(dto.getSexe());
        if (dto.getIndicatifPays() != null) agent.setIndicatifPays(dto.getIndicatifPays());
        if (dto.getNumTelephone() != null) agent.setNumTelephone(dto.getNumTelephone());
        if (dto.getMatricule() != null) agent.setMatricule(dto.getMatricule());
        if (dto.getFonction() != null) agent.setFonction(dto.getFonction());
        if (dto.getEmail() != null) agent.setEmail(dto.getEmail());
        if (dto.getPhotoUrl() != null) agent.setPhotoUrl(dto.getPhotoUrl());
    }

    private AgentResponseDTO toDTO(Agent agent) {
        AgentResponseDTO dto = new AgentResponseDTO();
        dto.setId(agent.getId());
        dto.setNom(agent.getNom());
        dto.setDateNaissance(agent.getDateNaissance());
        dto.setSexe(agent.getSexe());
        dto.setIndicatifPays(agent.getIndicatifPays());
        dto.setNumTelephone(agent.getNumTelephone());
        dto.setMatricule(agent.getMatricule());
        dto.setFonction(agent.getFonction());
        dto.setEmail(agent.getEmail());
        dto.setPhotoUrl(agent.getPhotoUrl());
        return dto;
    }
}
