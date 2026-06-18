package com.enspy.csi.service;

import com.enspy.csi.dto.request.AgentRequestDTO;
import com.enspy.csi.dto.response.AgentResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AgentService {
    AgentResponseDTO creerAgent(AgentRequestDTO dto);
    AgentResponseDTO getAgentById(Long id);
    AgentResponseDTO getAgentByEmail(String email);
    List<AgentResponseDTO> getAllAgents();
    AgentResponseDTO updateAgent(Long id, AgentRequestDTO dto);
    void deleteAgent(Long id);
    AgentResponseDTO uploadPhoto(Long id, MultipartFile photo);
}
