package com.enspy.csi.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ConsultationRequestDTO {
    private LocalDate date;
    private Long assureId;
    private Long generalisteId;
}
