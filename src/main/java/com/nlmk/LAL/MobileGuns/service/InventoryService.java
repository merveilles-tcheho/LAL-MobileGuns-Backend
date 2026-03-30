package com.nlmk.LAL.MobileGuns.service;

import java.util.List;

import com.nlmk.LAL.MobileGuns.dto.InventoryDTO;
import com.nlmk.LAL.MobileGuns.dto.InventoryScanRequestDTO;
import com.nlmk.LAL.MobileGuns.dto.InventoryScanResponseDTO;
import com.nlmk.LAL.MobileGuns.dto.YardRowDTO;

public interface InventoryService {
	
	 // Liste des inventaires ouverts
    List<InventoryDTO> getInventairesOuverts();

    // Parcs d'un inventaire
    List<YardRowDTO> getParcLoges(Integer numeroInv);

    // Scanner une bobine
    InventoryScanResponseDTO scannerBobine(
        InventoryScanRequestDTO request);

}
