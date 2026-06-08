package com.enspy.csi.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AssureResponseDTO {
    private Long id;
    private String idAssure;
    private String nom;
    private LocalDate dateNaissance;
    private String profession;
    private String groupeSanguin;
    private String email;
}
