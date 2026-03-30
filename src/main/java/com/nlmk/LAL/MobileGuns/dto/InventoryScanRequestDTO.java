package com.nlmk.LAL.MobileGuns.dto;

import lombok.Data;

@Data
public class InventoryScanRequestDTO {
	private Integer numeroInv;    
    private String parcInv;       
    private String logeInv;       
    private String position;      
    private String level;        
    private Integer coilSq;   

}
