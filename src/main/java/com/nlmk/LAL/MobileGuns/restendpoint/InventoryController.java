package com.nlmk.LAL.MobileGuns.restendpoint;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nlmk.LAL.MobileGuns.dto.InventoryDTO;
import com.nlmk.LAL.MobileGuns.dto.InventoryScanRequestDTO;
import com.nlmk.LAL.MobileGuns.dto.InventoryScanResponseDTO;
import com.nlmk.LAL.MobileGuns.dto.YardRowDTO;
import com.nlmk.LAL.MobileGuns.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // ── GET — Liste inventaires ouverts ───────────────────────
    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getInventairesOuverts() {

        List<InventoryDTO> inventaires =
            inventoryService.getInventairesOuverts();

        return ResponseEntity.ok(inventaires);
    }

    // ── GET — Parcs d'un inventaire ───────────────────────────
    @GetMapping("/{numeroInv}/parcloges")
    public ResponseEntity<List<YardRowDTO>> getParcLoges(
            @PathVariable Integer numeroInv) {

        List<YardRowDTO> parcLoges =
            inventoryService.getParcLoges(numeroInv);

        return ResponseEntity.ok(parcLoges);
    }

    // ── POST — Scanner une bobine ──────────────────────────────
    @PostMapping("/scan")
    public ResponseEntity<InventoryScanResponseDTO> scannerBobine(
            @RequestBody InventoryScanRequestDTO request) {

        InventoryScanResponseDTO response =
            inventoryService.scannerBobine(request);

        return ResponseEntity.ok(response);
    }
}
