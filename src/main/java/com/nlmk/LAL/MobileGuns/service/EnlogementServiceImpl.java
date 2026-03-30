package com.nlmk.LAL.MobileGuns.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nlmk.LAL.MobileGuns.dto.EnlogementRequestDTO;
import com.nlmk.LAL.MobileGuns.dto.EnlogementResponseDTO;
import com.nlmk.LAL.MobileGuns.entity.Coil;
import com.nlmk.LAL.MobileGuns.entity.CoilMission;
import com.nlmk.LAL.MobileGuns.entity.Mission;
import com.nlmk.LAL.MobileGuns.error.BusinessException;
import com.nlmk.LAL.MobileGuns.error.ResourceNotFoundException;
import com.nlmk.LAL.MobileGuns.repository.CoilInconnuRepository;
import com.nlmk.LAL.MobileGuns.repository.CoilMissionRepository;
import com.nlmk.LAL.MobileGuns.repository.CoilRepository;
import com.nlmk.LAL.MobileGuns.repository.MissionRepository;
import com.nlmk.LAL.MobileGuns.repository.RowRepository;
import com.nlmk.LAL.MobileGuns.repository.TransactionRepository;
import com.nlmk.LAL.MobileGuns.repository.YardRepository;
import com.nlmk.LAL.MobileGuns.tools.MyTools;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

@Service
public class EnlogementServiceImpl implements EnlogementService {

    @Autowired
    private CoilRepository coilRepository;

    @Autowired
    private YardRepository yardRepository;

    @Autowired
    private RowRepository logeRepository;

    @Autowired
    private CoilMissionRepository coilMissionRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private CoilInconnuRepository coilInconnuRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // ── Constantes ─────────────────────────────────
    private static final List<String> PARCS_1D = List.of(
        "TDR", "SKI", "QTO", "MGT", "REF"
    );

    private static final List<String> PARCS_COND = List.of(
        "TDR", "SKI", "MGT", "REF"
    );

    @Override
    @Transactional
    public EnlogementResponseDTO enloger(EnlogementRequestDTO request) {

        EnlogementResponseDTO response = new EnlogementResponseDTO();

        // ── Étape 1 — Vérifier bobine existe 
        
        Optional<Coil> coilOpt = coilRepository
            .findById(request.getCoilSq());

        if (coilOpt.isEmpty()) {
            throw new ResourceNotFoundException(
                "Bobine introuvable : " + request.getCoilSq());
        }

        Coil coil = coilOpt.get();

        // ── Étape 2 — Vérifier parc destination existe ────────
        if (yardRepository.findById(
                request.getParcDestination()).isEmpty()) {
            throw new BusinessException(
                "Parc introuvable : " + request.getParcDestination());
        }

        // ── Étape 3 — Vérifier loge destination existe ────────
        boolean logeExiste = logeRepository
            .findByIdYard(request.getParcDestination())
            .stream()
            .anyMatch(l -> l.getId().getRow()
                .equals(request.getLogeDestination()));

        if (!logeExiste) {
            throw new BusinessException(
                "Loge introuvable : " + request.getLogeDestination());
        }

        // ── Étape 3bis — Règle R2 : conditionnement ───────────
        gererConditionnement(coil, request);

        // ── Étape 4 — Règle R3 : bypass position/niveau ───────
        String position = request.getPosition();
        String niveau   = request.getNiveau();

        boolean parc1D = PARCS_1D.contains(request.getParcIni());

        if (parc1D) {
            position = null;
            niveau   = null;
        }

        // ── Étape 5 — Compter bobines sur position ─────────────
        Integer nbBobines = 0;

        if (!parc1D && position != null && niveau != null) {
            nbBobines = coilRepository.countBobinesPosition(
                request.getParcDestination(),
                request.getLogeDestination(),
                position,
                niveau
            );
        }

        // ── Étape 6 — Position occupée → retourner pour forçage
        if (nbBobines > 0 && !request.isForcer()) {
            response.setSucces(false);
            response.setPositionOccupee(true);
            response.setNombreBobinesPresentes(nbBobines);
            response.setMessage("Position déjà occupée. Forcer ?");
            return response;
        }

        // ── Étape 7 — Règle R5 : invalider avant forçage ──────
        if (nbBobines > 0 && request.isForcer()) {
            coilRepository.updatePositionInvalide(
                request.getParcDestination(),
                request.getLogeDestination(),
                position,
                niveau
            );
            MyTools.logInfo("R5 — Position invalidée avant forçage");
        }

        // ── Étape 8 — Règle R4 : choisir le bon UPDATE ────────
        effectuerEnlogement(
            coil,
            request.getParcDestination(),
            request.getLogeDestination(),
            position,
            niveau
        );

        // ── Étape 9 — DELETE T068COILS_INCONNU ────────────────
        if (!parc1D && position != null && niveau != null) {
            coilInconnuRepository.deleteByPosition(
                request.getParcDestination(),
                request.getLogeDestination(),
                position,
                niveau
            );
        }

        // ── Étape 10 — Règle R6 : mission DK1/DK2 ─────────────
        if ("DK1".equals(request.getParcDestination()) ||
            "DK2".equals(request.getParcDestination())) {
            gererMission(coil, request.getParcDestination(),
                         position, niveau);
        }

        // ── Étape 11 — INSERT traçabilité TTQ004 ───────────────
        transactionRepository.insertMatmod(request.getCoilSq());
        MyTools.logInfo("Traçabilité insérée pour coilSq : "
            + request.getCoilSq());

        // ── Étape 12 — Réponse succès ──────────────────────────
        response.setSucces(true);
        response.setMessage("Enlogement effectué avec succès");
        response.setCoilId(coil.getCoilId());
        response.setParcFinal(request.getParcDestination());
        response.setLogeFinal(request.getLogeDestination());
        response.setPositionFinal(position);
        response.setNiveauFinal(niveau);

        return response;
    }

    // ── Méthode privée — Règle R2 ─────────────────────────────
    private void gererConditionnement(Coil coil,
            EnlogementRequestDTO request) {

        boolean conditionR2 =
            "D".equals(request.getModeD()) &&
            PARCS_COND.contains(request.getParcIni()) &&
            coil.getPackagingDt() == null &&
            coil.getPackagingStrDt() == null;

        if (!conditionR2) {
            return;
        }

        String cond = request.getConditionnement();

        if ("O".equals(cond)) {
            if (coil.getPackagingDt() != null) {
                coilRepository.updateEmballageSansDate(
                    coil.getCoilSq(),
                    request.getEmballageCd(),
                    request.getProtectionRive(),
                    request.getFeuillardRad(),
                    request.getFeuillardCir()
                );
                gererAgid(coil, cond, true);

            } else {
                coilRepository.updateEmballageAvecDate(
                    coil.getCoilSq(),
                    request.getEmballageCd(),
                    request.getProtectionRive(),
                    request.getFeuillardRad(),
                    request.getFeuillardCir()
                );
                gererAgid(coil, cond, false);
            }
            MyTools.logInfo("R2 — Conditionnement O appliqué");

        } else if ("N".equals(cond)) {
            if (coil.getPackagingDt() != null) {
                coilRepository.updateEmballageNullDate(
                    coil.getCoilSq(),
                    request.getEmballageCd(),
                    request.getProtectionRive(),
                    request.getFeuillardRad(),
                    request.getFeuillardCir()
                );
                gererAgid(coil, cond, true);
            }
            MyTools.logInfo("R2 — Déconditionnement N appliqué");
        }
    }

    // ── Méthode privée — Règle R4 ─────────────────────────────
    private void effectuerEnlogement(Coil coil, String parc,
            String loge, String position, String niveau) {

        try {
            String site = (String) entityManager
                .createNativeQuery(
                    "SELECT ugfab.fct_get_site(:parc) FROM DUAL")
                .setParameter("parc", coil.getYard() != null ?
                    coil.getYard().getYard() : parc)
                .getSingleResult();

            String parcType = coil.getYard() != null ?
                coil.getYard().getType() : null;

            if ("LAL".equals(site) &&
                !"EXTERNE".equals(parcType) &&
                !"TRAS".equals(parc)) {

                coilRepository.updatePosition(
                    coil.getCoilSq(), parc, loge, position, niveau);
                MyTools.logInfo("R4 — updatePosition (site LAL)");

            } else {
                coilRepository.updatePositionResetShip(
                    coil.getCoilSq(), parc, loge, position, niveau);
                MyTools.logInfo("R4 — updatePositionResetShip");
            }

        } catch (Exception e) {
            MyTools.logError("R4 — fct_get_site erreur", e);
            coilRepository.updatePosition(
                coil.getCoilSq(), parc, loge, position, niveau);
        }
    }

    // ── Méthode privée — Règle R6 ─────────────────────────────
    private void gererMission(Coil coil, String parcDestination,
            String position, String niveau) {

        Optional<CoilMission> missionOpt = coilMissionRepository
            .findMissionOuverteByCoilSq(coil.getCoilSq());

        if (missionOpt.isEmpty()) {
            return;
        }

        CoilMission coilMission = missionOpt.get();

        Optional<Mission> missionObjOpt = missionRepository
            .findById(coilMission.getId().getMissionSq());

        if (missionObjOpt.isEmpty()) {
            return;
        }

        Mission mission = missionObjOpt.get();

        if (parcDestination.equals(mission.getDestination())) {
            coilMissionRepository.cloturerMission(
                coilMission.getId().getMissionSq(),
                coil.getCoilSq(),
                coilMission.getId().getCoilSq().toString(),
                niveau != null ? niveau : "",
                position != null ? position : ""
            );
            MyTools.logInfo("R6 — Mission clôturée : "
                + coilMission.getId().getMissionSq());
        }
    }

    // ── Méthode privée — Règle R7 ─────────────────────────────
    private void gererAgid(Coil coil, String conditionnement,
            boolean etaitEmballe) {

        try {
            StoredProcedureQuery query;

            if (!etaitEmballe) {
                query = entityManager.createStoredProcedureQuery(
                    "ugfab.prc_Return_Agid_Controlkey_ns");
            } else {
                query = entityManager.createStoredProcedureQuery(
                    "ugfab.prc_Return_Agid_Controlkey");
            }

            query.registerStoredProcedureParameter(
                1, Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(
                2, String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter(
                3, String.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter(
                4, String.class, ParameterMode.OUT);

            query.setParameter(1, coil.getCoilSq());
            query.setParameter(2, "LL_PACK");

            query.execute();

            String cagid  = (String) query
                .getOutputParameterValue(3);
            String vError = (String) query
                .getOutputParameterValue(4);

            MyTools.logInfo("R7 — AGID retourné : " + cagid);

            if (vError != null && !vError.isEmpty()) {
                MyTools.logError(
                    "R7 — Erreur AGID : " + vError, null);
                return;
            }

            if (cagid != null && !cagid.isEmpty()) {
                entityManager.createNativeQuery(
                    "UPDATE UGFAB.T001COILS " +
                    "SET CAGID = :cagid, " +
                    "CUPDATE_DT = SYSDATE, " +
                    "CUPDATE_NM = 'SCAN_ENLOGEMENT_AGID', " +
                    "CFONCTION_NM = 'GUN_SKI_TDR_AGID' " +
                    "WHERE CCOIL_SQ = :coilSq")
                    .setParameter("cagid", cagid)
                    .setParameter("coilSq", coil.getCoilSq())
                    .executeUpdate();

                MyTools.logInfo("R7 — CAGID mis à jour : " + cagid);
            }

        } catch (Exception e) {
            MyTools.logError("R7 — Erreur procédure AGID", e);
        }
    }
}