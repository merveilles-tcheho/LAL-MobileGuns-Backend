package com.nlmk.LAL.MobileGuns.service;

import java.util.ArrayList;
import com.nlmk.LAL.MobileGuns.dto.DashboardInventaireDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nlmk.LAL.MobileGuns.dto.DashboardBobineDTO;
import com.nlmk.LAL.MobileGuns.dto.DashboardLogeDTO;
import com.nlmk.LAL.MobileGuns.dto.DashboardMouvementDTO;
import com.nlmk.LAL.MobileGuns.dto.DashboardParcDTO;
import com.nlmk.LAL.MobileGuns.dto.DashboardStatsDTO;
import com.nlmk.LAL.MobileGuns.tools.MyTools;

import jakarta.persistence.EntityManager;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private EntityManager entityManager;

    // Déclaré au niveau de la classe
    private static final List<String> PARCS_1D =
        List.of("TDR", "SKI", "MGT", "REF", "QTO");

    @Override
    
    public DashboardStatsDTO getStats() {
        Number nbBobines = (Number) entityManager.createNativeQuery("""
            SELECT COUNT(*) FROM UGFAB.T001COILS
            WHERE CSOLDE_TY IS NULL AND CPARC IS NOT NULL
            """).getSingleResult();

        Number nbMouvements = (Number) entityManager.createNativeQuery("""
            SELECT COUNT(*) FROM UGFAB.T_GUN_LAL_MOV_LOG
            WHERE TRUNC(CINSERT_DT) = TRUNC(SYSDATE)
            """).getSingleResult();

        Number nbEnlogements = (Number) entityManager.createNativeQuery("""
            SELECT COUNT(*) FROM UGFAB.T_GUN_LAL_MOV_LOG
            WHERE TRUNC(CINSERT_DT) = TRUNC(SYSDATE)
            AND CFONCTION_NM = 'GUN_LAL_ENLOGEMENT'
            """).getSingleResult();

        Number nbInventaires = (Number) entityManager.createNativeQuery("""
            SELECT COUNT(*) FROM UGFAB.T070INVENTAIRES
            WHERE CCLOTURE_DT IS NULL
            """).getSingleResult();

        Number nbSoldees = (Number) entityManager.createNativeQuery("""
            SELECT COUNT(*) FROM UGFAB.T001COILS
            WHERE CSOLDE_TY IS NOT NULL AND CPARC IS NOT NULL
            """).getSingleResult();

        DashboardStatsDTO dto = new DashboardStatsDTO();
        dto.setNbTotalBobines(nbBobines.longValue());
        dto.setNbMouvementsJour(nbMouvements.longValue());
        dto.setNbEnlogementsJour(nbEnlogements.longValue());
        dto.setNbInventairesOuverts(nbInventaires.longValue());
        dto.setNbSoldees(nbSoldees.longValue());

        MyTools.logInfo("Dashboard — Stats chargées");
        return dto;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DashboardParcDTO> getParcs() {
    	List<Object[]> rows = entityManager.createNativeQuery("""
    	        SELECT CPARC, COUNT(*) as nb
    	        FROM UGFAB.T001COILS
    	        WHERE CSOLDE_TY IS NULL
    	        AND CPARC IN ('TDR', 'SKI', 'DK1', 'DK2', 'DKP' , 'QTO' , 'REF')
    	        GROUP BY CPARC ORDER BY CPARC
    	        """).getResultList();
        List<DashboardParcDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            DashboardParcDTO dto = new DashboardParcDTO();
            dto.setParc(row[0] != null ? row[0].toString() : "");
            dto.setNbBobines(((Number) row[1]).longValue());
            result.add(dto);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DashboardMouvementDTO> getDerniersMouvements() {
        List<Object[]> rows = entityManager.createNativeQuery("""
            SELECT CTYPE_ID || CCOIL_ID || CCOUPE_ID,
                   CPARC_FROM, CLOGE_FROM,
                   CPARC_TO, CLOGE_TO, CINSERT_DT
            FROM UGFAB.T_GUN_LAL_MOV_LOG
            WHERE TRUNC(CINSERT_DT) = TRUNC(SYSDATE)
            ORDER BY CINSERT_DT DESC
            FETCH FIRST 10 ROWS ONLY
            """).getResultList();

        List<DashboardMouvementDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            DashboardMouvementDTO dto = new DashboardMouvementDTO();
            dto.setCoilId(row[0] != null ? row[0].toString() : "");
            dto.setParcFrom(row[1] != null ? row[1].toString() : "");
            dto.setLogeFrom(row[2] != null ? row[2].toString() : "");
            dto.setParcTo(row[3] != null ? row[3].toString() : "");
            dto.setLogeTo(row[4] != null ? row[4].toString() : "");
            if (row[5] != null) {
                dto.setInsertDt(((java.sql.Date) row[5]).toLocalDate());
            }
            result.add(dto);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DashboardLogeDTO> getLogesByParc(String parc) {

        List<Object[]> rows = entityManager.createNativeQuery("""
            SELECT CLOGE, COUNT(*) as nb
            FROM UGFAB.T001COILS
            WHERE CPARC = :parc
            AND CSOLDE_TY IS NULL
            AND CLOGE IS NOT NULL
            GROUP BY CLOGE ORDER BY CLOGE
            """)
            .setParameter("parc", parc)
            .getResultList();

        List<Object[]> rowsSolde = entityManager.createNativeQuery("""
            SELECT CLOGE, COUNT(*) as nb
            FROM UGFAB.T001COILS
            WHERE CPARC = :parc
            AND CSOLDE_TY IS NOT NULL
            AND CLOGE IS NOT NULL
            GROUP BY CLOGE ORDER BY CLOGE
            """)
            .setParameter("parc", parc)
            .getResultList();

        List<Object[]> rowsCap = entityManager.createNativeQuery("""
            SELECT CLOGE, COUNT(*) as nb_niveaux
            FROM UGFAB.T912NIVEAU
            WHERE CPARC = :parc
            AND CLOGE IS NOT NULL
            GROUP BY CLOGE ORDER BY CLOGE
            """)
            .setParameter("parc", parc)
            .getResultList();

        Map<String, Long> mapSolde = new HashMap<>();
        for (Object[] r : rowsSolde) {
            mapSolde.put(r[0].toString(), ((Number) r[1]).longValue());
        }

        Map<String, Long> mapCap = new HashMap<>();
        for (Object[] r : rowsCap) {
            mapCap.put(r[0].toString(), ((Number) r[1]).longValue());
        }

        List<DashboardLogeDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            String loge = row[0].toString();
            long nb     = ((Number) row[1]).longValue();
            long solde  = mapSolde.getOrDefault(loge, 0L);

            long cap;
            double taux;

            if (PARCS_1D.contains(parc)) {
                // Parcs 1D — capacité max 25 bobines
                cap  = 100;
                taux = Math.min((nb * 100.0 / cap), 100); 
            } else {
                // DK1/DK2/DKP — nb niveaux × 13 positions
                long nbNiveaux = mapCap.getOrDefault(loge, 1L);
                cap  = nbNiveaux * 13;
                taux = Math.min(cap > 0 ? (nb * 100.0 / cap) : 0, 100); 
            }

            DashboardLogeDTO dto = new DashboardLogeDTO();
            dto.setParc(parc);
            dto.setLoge(loge);
            dto.setNbBobines(nb);
            dto.setNbSoldees(solde);
            dto.setCapacite(cap);
            dto.setTauxOccupation(Math.min(taux, 100));
            result.add(dto);
        }

        MyTools.logInfo("Dashboard — Loges chargées pour parc : " + parc);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DashboardBobineDTO> getBobinesByLoge(String parc, String loge) {
        List<Object[]> rows = entityManager.createNativeQuery("""
            SELECT CTYPE_ID || CCOIL_ID || CCOUPE_ID,
                   CTYPE_ID, CSOLDE_TY
            FROM UGFAB.T001COILS
            WHERE CPARC = :parc AND CLOGE = :loge
            ORDER BY CCOIL_ID
            """)
            .setParameter("parc", parc)
            .setParameter("loge", loge)
            .getResultList();

        List<DashboardBobineDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            DashboardBobineDTO dto = new DashboardBobineDTO();
            dto.setCoilId(row[0] != null ? row[0].toString() : "");

            String typeId = row[1] != null ? row[1].toString() : "";
            String solde  = row[2] != null ? row[2].toString() : null;

            if (solde != null) {
                dto.setType("SOLDE");
            } else if (typeId.equals("4") || typeId.equals("6")) {
                dto.setType("CHAUD");
            } else {
                dto.setType("FROID");
            }
            result.add(dto);
        }

        MyTools.logInfo("Dashboard — Bobines pour " + parc + "/" + loge);
        return result;
    }
 // ── État avancement inventaires 
    @Override
    @SuppressWarnings("unchecked")
    public List<DashboardInventaireDTO> getInventaires() {

        // Nb bobines scannées par inventaire/parc/loge
        List<Object[]> rowsScannes = entityManager.createNativeQuery("""
            SELECT ci.CNUMERO_INV, inv.CREMARQUE, ci.CPARC, ci.CLOGE,
                   COUNT(*) as nb_scannes
            FROM UGFAB.T071COILS_INVENTAIRE ci
            JOIN UGFAB.T070INVENTAIRES inv
                 ON ci.CNUMERO_INV = inv.CNUMERO_INV
            WHERE inv.CCLOTURE_DT IS NULL
            AND ci.CPARC IS NOT NULL
            AND ci.CLOGE IS NOT NULL
            GROUP BY ci.CNUMERO_INV, inv.CREMARQUE, ci.CPARC, ci.CLOGE
            ORDER BY ci.CNUMERO_INV, ci.CPARC, ci.CLOGE
            """).getResultList();

        // Nb bobines attendues par parc/loge (stock actuel)
        List<Object[]> rowsAttendu = entityManager.createNativeQuery("""
            SELECT CPARC, CLOGE, COUNT(*) as nb_attendu
            FROM UGFAB.T001COILS
            WHERE CSOLDE_TY IS NULL
            AND CPARC IS NOT NULL
            AND CLOGE IS NOT NULL
            GROUP BY CPARC, CLOGE
            ORDER BY CPARC, CLOGE
            """).getResultList();

        // Map nb attendu par parc+loge
        java.util.Map<String, Long> mapAttendu = new java.util.HashMap<>();
        for (Object[] r : rowsAttendu) {
            String key = r[0].toString() + "_" + r[1].toString();
            mapAttendu.put(key, ((Number) r[2]).longValue());
        }

        List<DashboardInventaireDTO> result = new java.util.ArrayList<>();
        for (Object[] row : rowsScannes) {
            String parc  = row[2] != null ? row[2].toString() : "";
            String loge  = row[3] != null ? row[3].toString() : "";
            long nbScan  = ((Number) row[4]).longValue();
            long nbAtt   = mapAttendu.getOrDefault(parc + "_" + loge, 0L);
            double taux  = nbAtt > 0 ? Math.min((nbScan * 100.0 / nbAtt), 100) : 0;

            DashboardInventaireDTO dto = new DashboardInventaireDTO();
            dto.setNumeroInv(row[0] != null ? row[0].toString() : "");
            dto.setRemark(row[1] != null ? row[1].toString() : "");
            dto.setParc(parc);
            dto.setLoge(loge);
            dto.setNbScannes(nbScan);
            dto.setNbAttendu(nbAtt);
            dto.setTauxAvancement(taux);
            result.add(dto);
        }

        MyTools.logInfo("Dashboard — Inventaires chargés : " + result.size());
        return result;
    }
}