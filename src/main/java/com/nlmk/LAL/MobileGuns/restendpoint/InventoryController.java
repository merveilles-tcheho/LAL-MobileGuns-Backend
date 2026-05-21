package com.nlmk.LAL.MobileGuns.restendpoint;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nlmk.LAL.MobileGuns.dto.InventoryDTO;
import com.nlmk.LAL.MobileGuns.dto.InventoryScanRequestDTO;
import com.nlmk.LAL.MobileGuns.dto.InventoryScanResponseDTO;
import com.nlmk.LAL.MobileGuns.dto.YardRowDTO;
import com.nlmk.LAL.MobileGuns.entity.Coil;
import com.nlmk.LAL.MobileGuns.repository.CoilRepository;
import com.nlmk.LAL.MobileGuns.service.InventoryService;
import jakarta.persistence.EntityManager;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private CoilRepository coilRepository;

    @Autowired
    private EntityManager entityManager; 

    // ── GET — Liste inventaires ouverts
    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getInventairesOuverts() {
        List<InventoryDTO> inventaires = inventoryService.getInventairesOuverts();
        return ResponseEntity.ok(inventaires);
    }

    // ── GET — Parcs d'un inventaire
    @GetMapping("/{numeroInv}/parcloges")
    public ResponseEntity<List<YardRowDTO>> getParcLoges(
            @PathVariable String numeroInv) {
        List<YardRowDTO> parcLoges = inventoryService.getParcLoges(numeroInv);
        return ResponseEntity.ok(parcLoges);
    }

    // ── GET — Dernier scan d'une loge  requête native
    @SuppressWarnings("unchecked")
    @GetMapping("/{numeroInv}/lastScan")
    public ResponseEntity<InventoryScanResponseDTO> getDernierScan(
            @PathVariable String numeroInv,
            @RequestParam String parc,
            @RequestParam String loge) {

        //  Dernière bobine scannée via SQL natif
    	List<Object[]> rows = entityManager.createNativeQuery("""
    		    SELECT CCOIL_SQ, CPARC, CLOGE
    		    FROM UGFAB.T071COILS_INVENTAIRE
    		    WHERE CNUMERO_INV = :numeroInv
    		    AND CPARC = :parc
    		    AND CLOGE = :loge
    		    ORDER BY COALESCE(CSCAN_DT, CINSERT_DT) DESC NULLS LAST
    		    FETCH FIRST 1 ROWS ONLY
    		    """)
    		    .setParameter("numeroInv", numeroInv)
    		    .setParameter("parc", parc)
    		    .setParameter("loge", loge)
    		    .getResultList();
        if (rows.isEmpty()) {
            return ResponseEntity.ok(null);
        }

        // ✅ Compter total bobines de la loge
        List<Object> countRows = entityManager.createNativeQuery("""
            SELECT COUNT(*)
            FROM UGFAB.T071COILS_INVENTAIRE
            WHERE CNUMERO_INV = :numeroInv
            AND CPARC = :parc
            AND CLOGE = :loge
            """)
            .setParameter("numeroInv", numeroInv)
            .setParameter("parc", parc)
            .setParameter("loge", loge)
            .getResultList();

        int nbCoils = countRows.isEmpty() ? 0 :
            ((Number) countRows.get(0)).intValue();

        // ✅ Récupérer coilId complet depuis T001COILS
        Object[] row = rows.get(0);
        Integer coilSq = ((Number) row[0]).intValue();

        Optional<Coil> coilOpt = coilRepository.findById(coilSq);
        String codeComplet = "";
        if (coilOpt.isPresent()) {
            Coil coil = coilOpt.get();
            codeComplet = coil.getTypeId()
                + coil.getCoilId()
                + coil.getCoupeId();
        }

        InventoryScanResponseDTO response = new InventoryScanResponseDTO();
        response.setCoilId(codeComplet);
        response.setYard(parc);
        response.setRow(loge);
        response.setNbCoilsInv(nbCoils);
        response.setSucces(true);
        response.setMessage("Dernière bobine scannée");
        response.setDejaScanne(false);

        return ResponseEntity.ok(response);
    }

    // ── POST — Scanner une bobine
    @PostMapping("/scan")
    public ResponseEntity<InventoryScanResponseDTO> scannerBobine(
            @RequestBody InventoryScanRequestDTO request) {
        InventoryScanResponseDTO response = inventoryService.scannerBobine(request);
        return ResponseEntity.ok(response);
    }
}