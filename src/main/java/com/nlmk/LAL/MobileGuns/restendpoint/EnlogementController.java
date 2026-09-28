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
import com.nlmk.LAL.MobileGuns.dto.EnlogementRequestDTO;
import com.nlmk.LAL.MobileGuns.dto.EnlogementResponseDTO;
import com.nlmk.LAL.MobileGuns.entity.MovLog;
import com.nlmk.LAL.MobileGuns.repository.MovLogRepository;
import com.nlmk.LAL.MobileGuns.service.EnlogementService;

@RestController
@RequestMapping("/api/enlogement")
public class EnlogementController {

    @Autowired
    private EnlogementService enlogementService;

    @Autowired
    private MovLogRepository movLogRepository;

    private static final List<String> PARCS_DK = List.of("DK1", "DK2", "DKP");

    @PostMapping
    public ResponseEntity<EnlogementResponseDTO> enloger(
            @RequestBody EnlogementRequestDTO request) {
        EnlogementResponseDTO response = enlogementService.enloger(request);
        return ResponseEntity.ok(response);
    }

    // ── Vérifier si bobine déjà déplacée
    @GetMapping("/check/{coilSq}")
    public ResponseEntity<EnlogementResponseDTO> checkDejaDeplace(
            @PathVariable Integer coilSq) {

        EnlogementResponseDTO response = new EnlogementResponseDTO();

        List<MovLog> logs = movLogRepository
            .findByCoilSqOrderByInsertDtDesc(coilSq);

        if (!logs.isEmpty()) {
            MovLog dernierLog = logs.get(0);

            //  Convertir Integer en String pour lit
            String from = buildPosition(
                dernierLog.getParcFrom(),
                dernierLog.getLogeFrom(),
                dernierLog.getPileFrom(),
                dernierLog.getLitFrom() != null ?
                    dernierLog.getLitFrom().toString() : null
            );

            String to = buildPosition(
                dernierLog.getParcTo(),
                dernierLog.getLogeTo(),
                dernierLog.getPileTo(),
                dernierLog.getLitTo() != null ?
                    dernierLog.getLitTo().toString() : null
            );

            response.setDejaDeplace(true);
            response.setMessageAlerte(
                "Attention cette bobine a déjà été déplacée " +
                "(de " + from + " vers " + to + ")"
            );
        } else {
            response.setDejaDeplace(false);
        }

        return ResponseEntity.ok(response);
    }

    // Construire la position complète selon le parc
    private String buildPosition(String parc, String loge,
                                  String pile, String lit) {
        if (parc == null) return "";
        StringBuilder pos = new StringBuilder(parc);
        if (loge != null) pos.append(".").append(loge);

        // Si DK1, DK2 ou DKP — ajouter pile et lit
        if (PARCS_DK.contains(parc)) {
            if (pile != null && !pile.isEmpty()) {
                pos.append(".").append(pile);
            }
            if (lit != null && !lit.isEmpty()) {
                pos.append(".").append(lit);
            }
        }
        return pos.toString();
    }
}