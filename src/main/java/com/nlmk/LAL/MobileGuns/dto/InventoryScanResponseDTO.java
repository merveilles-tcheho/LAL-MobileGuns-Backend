package com.nlmk.LAL.MobileGuns.dto;

import lombok.Data;

@Data
public class InventoryScanResponseDTO {
	
	 private boolean succes;
	    private String message;
	    private boolean dejaScanne;     
	    private Integer nbCoilsInv;      
	    private String coilId;
	    private String yard;
	    private String row;
	    private String position ;
	    private String level;

}
