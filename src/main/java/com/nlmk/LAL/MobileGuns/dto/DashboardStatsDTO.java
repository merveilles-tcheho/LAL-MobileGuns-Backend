package com.nlmk.LAL.MobileGuns.dto;

import lombok.Data;

@Data
public class DashboardStatsDTO {
    private long nbTotalBobines;
    private long nbMouvementsJour;
    private long nbEnlogementsJour;
    private long nbInventairesOuverts;
    private long nbSoldees;
}