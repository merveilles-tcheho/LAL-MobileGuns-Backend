package com.nlmk.LAL.MobileGuns.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DashboardMouvementDTO {
    private String coilId;
    private String parcFrom;
    private String logeFrom;
    private String parcTo;
    private String logeTo;
    private LocalDate insertDt;
}