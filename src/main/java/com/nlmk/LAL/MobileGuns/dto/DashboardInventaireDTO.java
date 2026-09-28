package com.nlmk.LAL.MobileGuns.dto;

import lombok.Data;

@Data
public class DashboardInventaireDTO {
    private String numeroInv;
    private String remark;
    private String parc;
    private String loge;
    private long nbScannes;
    private long nbAttendu;
    private double tauxAvancement;
}
