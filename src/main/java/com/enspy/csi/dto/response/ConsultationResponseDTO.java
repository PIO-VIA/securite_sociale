package com.enspy.csi.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ConsultationResponseDTO {
    private Long id;
    private LocalDate date;
    private Long assureId;
    private Long generalisteId;
}
