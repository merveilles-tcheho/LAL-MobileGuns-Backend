package com.nlmk.LAL.MobileGuns.restendpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nlmk.LAL.MobileGuns.dto.IdentificationDTO;
import com.nlmk.LAL.MobileGuns.service.IdentificationService;

@RestController
@RequestMapping("/api/identification")
public class IdentificationController {

    @Autowired
    private IdentificationService identificationService;

    // ── Endpoint avec les 3 champs ─────────────────
    @GetMapping("/{typeId}/{coilId}/{coupeId}")
    public ResponseEntity<IdentificationDTO> identifier(
            @PathVariable String typeId,
            @PathVariable String coilId,
            @PathVariable String coupeId) {

        IdentificationDTO dto = identificationService
            .identifier(typeId, coilId, coupeId);
        return ResponseEntity.ok(dto);
    }
}