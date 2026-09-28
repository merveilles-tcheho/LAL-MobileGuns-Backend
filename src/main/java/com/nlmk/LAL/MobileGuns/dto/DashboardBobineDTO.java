package com.nlmk.LAL.MobileGuns.dto;

import lombok.Data;

@Data
public class DashboardBobineDTO {
    private String coilId;
    private String type; // CHAUD, FROID, SOLDE
}