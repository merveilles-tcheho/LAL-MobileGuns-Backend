package com.nlmk.LAL.MobileGuns.dto;

import lombok.Data;

@Data
public class IdentificationDTO {

    private Integer coilSq;
    private String coilId;
    private String coupeId;
    private String typeId;
    private String temperature;
    private String parc;
    private String loge;
    private Double epaisseur;
    private Double largeur;
    private Double poidsNet;
    private String orderSq;
    private String posteCde;
    private String quality;
    private String choice;
    private String pile;
    private String clit;

    //format DD/MM/YYYY
    
    private String packagingDt;
    private String packagingStrDt;

    private boolean solde;
    private String soldeDt;

    private String commande;
    private String gamme;
}