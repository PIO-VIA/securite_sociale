package com.enspy.csi.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ConsultationResponseDTO {
    private Long id;
    private LocalDate date;
    private Long assureId;
    private String assureNom;
    private String assureIdAssure;
    private Long generalisteId;
    private String generalisteNom;
    private String generalisteMatricule;
    private Integer nombrePrescriptions;
    private Boolean possedeFeuilleMaladie;
}
