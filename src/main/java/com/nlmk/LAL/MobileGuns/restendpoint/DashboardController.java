package com.nlmk.LAL.MobileGuns.restendpoint;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nlmk.LAL.MobileGuns.dto.DashboardBobineDTO;
import com.nlmk.LAL.MobileGuns.dto.DashboardLogeDTO;
import com.nlmk.LAL.MobileGuns.dto.DashboardMouvementDTO;
import com.nlmk.LAL.MobileGuns.dto.DashboardParcDTO;
import com.nlmk.LAL.MobileGuns.dto.DashboardStatsDTO;
import com.nlmk.LAL.MobileGuns.service.DashboardService;
import com.nlmk.LAL.MobileGuns.dto.DashboardInventaireDTO;
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // ── GET — Stats globales
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    // ── GET — Répartition par parc
    @GetMapping("/parcs")
    public ResponseEntity<List<DashboardParcDTO>> getParcs() {
        return ResponseEntity.ok(dashboardService.getParcs());
    }

    // ── GET — Derniers mouvements du jour
    @GetMapping("/mouvements")
    public ResponseEntity<List<DashboardMouvementDTO>> getDerniersMouvements() {
        return ResponseEntity.ok(dashboardService.getDerniersMouvements());
    }

    
    @GetMapping("/loges/{parc}")
    public ResponseEntity<List<DashboardLogeDTO>> getLogesByParc(
            @PathVariable String parc) {
        return ResponseEntity.ok(dashboardService.getLogesByParc(parc));
    }

    // ── GET — Bobines d'une loge 
    @GetMapping("/bobines/{parc}/{loge}")
    public ResponseEntity<List<DashboardBobineDTO>> getBobinesByLoge(
            @PathVariable String parc,
            @PathVariable String loge) {
        return ResponseEntity.ok(dashboardService.getBobinesByLoge(parc, loge));
    }
 // ── GET — État avancement inventaires 
    @GetMapping("/inventaires")
    public ResponseEntity<List<DashboardInventaireDTO>> getInventaires() {
        return ResponseEntity.ok(dashboardService.getInventaires());
    }
}