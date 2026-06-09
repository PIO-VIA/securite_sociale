package com.enspy.csi.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequestDTO {
    private String ancienMotDePasse;
    private String nouveauMotDePasse;
}
