package com.nlmk.LAL.MobileGuns.restendpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nlmk.LAL.MobileGuns.dto.EnlogementRequestDTO;
import com.nlmk.LAL.MobileGuns.dto.EnlogementResponseDTO;
import com.nlmk.LAL.MobileGuns.service.EnlogementService;

@RestController
@RequestMapping("/api/enlogement")
public class EnlogementController {

    @Autowired
    private EnlogementService enlogementService;

    @PostMapping
    public ResponseEntity<EnlogementResponseDTO> enloger(
            @RequestBody EnlogementRequestDTO request) {

        EnlogementResponseDTO response = enlogementService.enloger(request);
        return ResponseEntity.ok(response);
    }
}
