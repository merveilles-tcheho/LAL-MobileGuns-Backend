package com.nlmk.LAL.MobileGuns.service;

import java.time.LocalDateTime;
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
import com.nlmk.LAL.MobileGuns.entity.MovLog;
import com.nlmk.LAL.MobileGuns.error.BusinessException;
import com.nlmk.LAL.MobileGuns.error.ResourceNotFoundException;
import com.nlmk.LAL.MobileGuns.repository.CoilInconnuRepository;
import com.nlmk.LAL.MobileGuns.repository.CoilMissionRepository;
import com.nlmk.LAL.MobileGuns.repository.CoilRepository;
import com.nlmk.LAL.MobileGuns.repository.MissionRepository;
import com.nlmk.LAL.MobileGuns.repository.MovLogRepository;
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
    @Autowired
    private MovLogRepository movLogRepository; 

    @PersistenceContext
    private EntityManager entityManager;

    private static final List<String> PARCS_1D =
        List.of("TDR", "SKI", "QTO", "MGT", "REF");
    private static final List<String> PARCS_COND =
        List.of("TDR", "SKI", "MGT", "REF");

    @Override
    @Transactional
    public EnlogementResponseDTO enloger(EnlogementRequestDTO request) {

        EnlogementResponseDTO response = new EnlogementResponseDTO();

        // ── Désactiver le trigger TRPSI02_A_U
        try {
            entityManager.createNativeQuery(
                "BEGIN ugfab.PKG039_TRIGGER_EXECUTION." +
                "DO_J001_Set_Execution_Flag('TRPSI02_A_U', FALSE); END;")
                .executeUpdate();
            MyTools.logInfo("Trigger TRPSI02_A_U désactivé");
        } catch (Exception e) {
            MyTools.logError("Erreur désactivation trigger", e);
        }

        // ── Étape 1 — Vérifier bobine existe
        Optional<Coil> coilOpt = coilRepository.findById(request.getCoilSq());
        if (coilOpt.isEmpty()) {
            throw new ResourceNotFoundException(
                "Bobine introuvable : " + request.getCoilSq());
        }
        Coil coil = coilOpt.get();

        // ──  Vérifier si bobine déjà déplacée (log)
        List<MovLog> logs = movLogRepository
            .findByCoilSqOrderByInsertDtDesc(request.getCoilSq());

        if (!logs.isEmpty()) {
            MovLog dernierLog = logs.get(0);
            String from = dernierLog.getParcFrom() + "." +
                          dernierLog.getLogeFrom();
            String to   = dernierLog.getParcTo() + "." +
                          dernierLog.getLogeTo();
            response.setDejaDeplace(true);
            response.setMessageAlerte(
                "Attention cette bobine a déjà été déplacée " +
                "(de " + from + " vers " + to + ")"
            );
        }

        // ── Étape 2 — Vérifier parc destination existe
        if (yardRepository.findById(request.getParcDestination()).isEmpty()) {
            throw new BusinessException(
                "Parc introuvable : " + request.getParcDestination());
        }

        // ── Étape 3 — Vérifier loge destination existe
        boolean logeExiste = logeRepository
            .findByIdYard(request.getParcDestination()).stream()
            .anyMatch(l -> l.getId().getRow()
                .equals(request.getLogeDestination()));
        if (!logeExiste) {
            throw new BusinessException(
                "Loge introuvable : " + request.getLogeDestination());
        }

        // ── Règle R7 — Même emplacement (parcs 1D uniquement)
        if (PARCS_1D.contains(request.getParcIni()) &&
            coil.getYard() != null &&
            coil.getYard().getYard().equals(request.getParcDestination()) &&
            coil.getLoge() != null &&
            coil.getLoge().getId().getRow().equals(request.getLogeDestination())) {
            throw new BusinessException(
                "Bobine déjà à cet emplacement !");
        }

        // ── Étape 3bis — Règle R4 : conditionnement
        gererConditionnement(coil, request);

        // ── Étape 4 — Règle R3 : bypass position/niveau
        boolean parc1D = PARCS_1D.contains(request.getParcIni());

        String position = request.getPosition();
        String niveau   = request.getNiveau();

        if (parc1D) {
            position = null;
            niveau   = null;
        } else {
            if (position == null || position.trim().isEmpty()) {
                throw new BusinessException(
                    "Position obligatoire pour le parc : " +
                    request.getParcDestination());
            }
            if (niveau == null || niveau.trim().isEmpty()) {
                throw new BusinessException(
                    "Niveau obligatoire pour le parc : " +
                    request.getParcDestination());
            }

            Number positionCount = (Number) entityManager
                .createNativeQuery(
                    "SELECT COUNT(*) FROM UGFAB.T913POSITION " +
                    "WHERE CPARC = :parc " +
                    "AND CLOGE = :loge " +
                    "AND CPOSITION = :position")
                .setParameter("parc", request.getParcDestination())
                .setParameter("loge", request.getLogeDestination())
                .setParameter("position", position)
                .getSingleResult();

            if (positionCount.intValue() == 0) {
                throw new BusinessException(
                    "Position introuvable : " + position);
            }

            Number niveauCount = (Number) entityManager
                .createNativeQuery(
                    "SELECT COUNT(*) FROM UGFAB.T912NIVEAU " +
                    "WHERE CPARC = :parc " +
                    "AND CLOGE = :loge " +
                    "AND CNIVEAU = :niveau")
                .setParameter("parc", request.getParcDestination())
                .setParameter("loge", request.getLogeDestination())
                .setParameter("niveau", niveau)
                .getSingleResult();

            if (niveauCount.intValue() == 0) {
                throw new BusinessException(
                    "Niveau introuvable : " + niveau);
            }
        }

        // ── Étape 5 — Compter bobines sur position
        Integer nbBobines = 0;
        if (!parc1D && position != null && niveau != null) {
            nbBobines = coilRepository.countBobinesPosition(
                request.getParcDestination(),
                request.getLogeDestination(),
                position, niveau);
        }

        // ── Étape 6 — Position occupée → retourner pour forçage
        if (nbBobines > 0 && !request.isForcer()) {
            response.setSucces(false);
            response.setPositionOccupee(true);
            response.setNombreBobinesPresentes(nbBobines);
            response.setMessage("Position déjà occupée. Forcer ?");
            return response;
        }

        // ── Étape 7 — Règle R9 : invalider avant forçage
        if (nbBobines > 0 && request.isForcer()) {
            coilRepository.updatePositionInvalide(
                request.getParcDestination(),
                request.getLogeDestination(),
                position, niveau);
            MyTools.logInfo("R9 — Position invalidée avant forçage");
        }

        // ── Sauvegarder position FROM avant enlogement
        String parcFrom = coil.getYard() != null ?
            coil.getYard().getYard() : null;
        String logeFrom = coil.getLoge() != null ?
            coil.getLoge().getId().getRow() : null;
        String pileFrom = coil.getCpile();
        Integer litFrom = coil.getClit() != null ?
            Integer.parseInt(coil.getClit()) : null;

        // ── Étape 8 — Règle R10 : choisir le bon UPDATE
        effectuerEnlogement(coil,
            request.getParcDestination(),
            request.getLogeDestination(),
            position, niveau);
        
     // ── INSERT dans T_GUN_LAL_MOV_LOG
        
        try {
            MovLog log = new MovLog();
            log.setCoilSq(coil.getCoilSq());
            log.setTypeId(coil.getTypeId());
            log.setCoilId(coil.getCoilId());
            log.setCoupeId(coil.getCoupeId());
            log.setParcFrom(parcFrom);
            log.setLogeFrom(logeFrom);
            log.setPileFrom(pileFrom);
            log.setLitFrom(litFrom);
            log.setParcTo(request.getParcDestination());
            log.setLogeTo(request.getLogeDestination());
            
            //  Ajouter pileTo et litTo
            
            log.setPileTo(position);
            log.setLitTo(niveau != null ? Integer.parseInt(niveau) : null);
            log.setInsertNm("SCAN_ENLOGEMENT");
            log.setInsertDt(LocalDateTime.now());
            log.setUpdateNm("SCAN_ENLOGEMENT");
            log.setUpdateDt(LocalDateTime.now());
            log.setFonctionNm("GUN_LAL_ENLOGEMENT");
            movLogRepository.save(log);
        } catch (Exception e) {
            MyTools.logError("MovLog — Erreur INSERT", e);
        }

        // ── Étape 9 — Règle R11 : DELETE T068COILS_INCONNU
        if (!parc1D && position != null && niveau != null) {
            coilInconnuRepository.deleteByPosition(
                request.getParcDestination(),
                request.getLogeDestination(),
                position, niveau);
        }

        // ── Règle R12 : mission DK1/DK2
        if ("DK1".equals(request.getParcDestination()) ||
            "DK2".equals(request.getParcDestination())) {
            gererMission(coil,
                request.getParcDestination(),
                request.getLogeDestination(),
                position, niveau);
        }

        // ── Règle R13 : INSERT traçabilité TTQ004
        transactionRepository.insertMatmod(request.getCoilSq());
        MyTools.logInfo("R13 — Traçabilité insérée pour coilSq : " +
            request.getCoilSq());

        // ── Réponse succès
        response.setSucces(true);
        response.setMessage("Enlogement effectué avec succès");
        response.setCoilId(coil.getCoilId());
        response.setParcFinal(request.getParcDestination());
        response.setLogeFinal(request.getLogeDestination());
        response.setPositionFinal(position);
        response.setNiveauFinal(niveau);

        return response;
    }

    // ── Règle R4 — Conditionnement ────────────────
    private void gererConditionnement(Coil coil,
            EnlogementRequestDTO request) {

        boolean conditionR2 = "D".equals(request.getModeD())
            && PARCS_COND.contains(request.getParcIni())
            && coil.getPackagingDt() == null
            && coil.getPackagingStrDt() == null;

        if (!conditionR2) return;

        String cond = request.getConditionnement();

        if ("O".equals(cond)) {
            if (coil.getPackagingDt() != null) {
                coilRepository.updateEmballageSansDate(
                    coil.getCoilSq(), request.getEmballageCd(),
                    request.getProtectionRive(),
                    request.getFeuillardRad(),
                    request.getFeuillardCir());
                gererAgid(coil, cond, true);
                transactionRepository.insertMatmod(coil.getCoilSq());
            } else {
                coilRepository.updateEmballageAvecDate(
                    coil.getCoilSq(), request.getEmballageCd(),
                    request.getProtectionRive(),
                    request.getFeuillardRad(),
                    request.getFeuillardCir());
                String cagid = gererAgidAvecRetour(coil, false);
                if (cagid != null && !cagid.isEmpty()) {
                    transactionRepository.insertMatprod(
                        coil.getCoilSq(), "E|" + cagid + "|N");
                }
                transactionRepository.insertMatmod(coil.getCoilSq());
            }
            MyTools.logInfo("R4 — Conditionnement O appliqué");

        } else if ("N".equals(cond)) {
            if (coil.getPackagingDt() != null) {
                coilRepository.updateEmballageNullDate(
                    coil.getCoilSq(), request.getEmballageCd(),
                    request.getProtectionRive(),
                    request.getFeuillardRad(),
                    request.getFeuillardCir());
                gererAgid(coil, cond, true);
                transactionRepository.insertMatalloc(coil.getCoilSq());
            }
            MyTools.logInfo("R4 — Déconditionnement N appliqué");
        }
    }

    // ── Règle R10 — Enlogement ────────────────────
    private void effectuerEnlogement(Coil coil, String parc,
            String loge, String position, String niveau) {
        try {
            String site = (String) entityManager
                .createNativeQuery(
                    "SELECT ugfab.fct_get_site(:parc) FROM DUAL")
                .setParameter("parc",
                    coil.getYard() != null ?
                    coil.getYard().getYard() : parc)
                .getSingleResult();

            String parcType = coil.getYard() != null ?
                coil.getYard().getType() : null;

            if ("LAL".equals(site) &&
                !"EXTERNE".equals(parcType) &&
                !"TRAS".equals(parc)) {
                coilRepository.updatePosition(
                    coil.getCoilSq(), parc, loge,
                    position, niveau);
                MyTools.logInfo("R10 — updatePosition (site LAL)");
            } else {
                coilRepository.updatePositionResetShip(
                    coil.getCoilSq(), parc, loge,
                    position, niveau);
                MyTools.logInfo("R10 — updatePositionResetShip");
            }
        } catch (Exception e) {
            MyTools.logError("R10 — fct_get_site erreur", e);
            coilRepository.updatePosition(
                coil.getCoilSq(), parc, loge,
                position, niveau);
        }
    }

    // ── Règle R12 — Mission DK1/DK2 ───────────────
    private void gererMission(Coil coil,
            String parcDestination,
            String logeDestination,
            String position, String niveau) {

        Optional<CoilMission> missionOpt =
            coilMissionRepository
                .findMissionOuverteByCoilSq(coil.getCoilSq());
        if (missionOpt.isEmpty()) return;

        CoilMission coilMission = missionOpt.get();

        Optional<Mission> missionObjOpt =
            missionRepository.findById(
                coilMission.getId().getMissionSq());
        if (missionObjOpt.isEmpty()) return;

        Mission mission = missionObjOpt.get();

        if (parcDestination.equals(mission.getDestination())) {
            coilMissionRepository.cloturerMission(
                coilMission.getId().getMissionSq(),
                coil.getCoilSq(),
                logeDestination,
                niveau != null ? niveau : "",
                position != null ? position : "");
            MyTools.logInfo("R12 — Mission clôturée : " +
                coilMission.getId().getMissionSq());
        }
    }

    // ── AGID sans retour ──────────────────────────
    private void gererAgid(Coil coil,
            String conditionnement, boolean etaitEmballe) {
        gererAgidAvecRetour(coil, etaitEmballe);
    }

    // ── AGID avec retour du cagid ─────────────────
    private String gererAgidAvecRetour(Coil coil,
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

            String cagid = (String) query
                .getOutputParameterValue(3);
            String vError = (String) query
                .getOutputParameterValue(4);

            MyTools.logInfo("AGID retourné : " + cagid);

            if (vError != null && !vError.isEmpty()) {
                MyTools.logError("Erreur AGID : " + vError, null);
                return null;
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
                MyTools.logInfo("CAGID mis à jour : " + cagid);
            }
            return cagid;

        } catch (Exception e) {
            MyTools.logError("Erreur procédure AGID", e);
            return null;
        }
    }
}