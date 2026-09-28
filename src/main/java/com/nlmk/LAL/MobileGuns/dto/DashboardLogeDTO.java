package com.nlmk.LAL.MobileGuns.dto;

import lombok.Data;

@Data
public class DashboardLogeDTO {
    private String parc;
    private String loge;
    private long nbBobines;
    private long nbSoldees;
    private long capacite;
    private double tauxOccupation;
}