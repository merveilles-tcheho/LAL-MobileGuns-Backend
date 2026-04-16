package com.nlmk.LAL.MobileGuns.dto;

import java.time.LocalDate;

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

	    private LocalDate packagingDt;
	    private LocalDate packagingStrDt; 

	    private boolean solde;
	    private String soldeDt;
	    
	    private String commande;  
	    private String gamme; 

}
