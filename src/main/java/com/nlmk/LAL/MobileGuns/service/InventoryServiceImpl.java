package com.nlmk.LAL.MobileGuns.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nlmk.LAL.MobileGuns.dto.InventoryDTO;
import com.nlmk.LAL.MobileGuns.dto.InventoryScanRequestDTO;
import com.nlmk.LAL.MobileGuns.dto.InventoryScanResponseDTO;
import com.nlmk.LAL.MobileGuns.dto.YardRowDTO;
import com.nlmk.LAL.MobileGuns.entity.Coil;
import com.nlmk.LAL.MobileGuns.entity.Inventory;
import com.nlmk.LAL.MobileGuns.entity.ParcLoge;
import com.nlmk.LAL.MobileGuns.entity.PostOder;
import com.nlmk.LAL.MobileGuns.error.BusinessException;
import com.nlmk.LAL.MobileGuns.error.ResourceNotFoundException;
import com.nlmk.LAL.MobileGuns.repository.CoilInventoryRepository;
import com.nlmk.LAL.MobileGuns.repository.CoilRepository;
import com.nlmk.LAL.MobileGuns.repository.InventoryRepository;
import com.nlmk.LAL.MobileGuns.repository.PostOderRepository;
import com.nlmk.LAL.MobileGuns.repository.YardRowRepository;
import com.nlmk.LAL.MobileGuns.tools.MyTools;
import jakarta.persistence.EntityManager;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private YardRowRepository yardRowRepository;

    @Autowired
    private CoilRepository coilRepository;

    @Autowired
    private CoilInventoryRepository coilInventoryRepository;

    @Autowired
    private PostOderRepository postOderRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<InventoryDTO> getInventairesOuverts() {
        List<Inventory> inventaires = inventoryRepository.findByClotureDtIsNull();
        return inventaires.stream().map(inv -> {
            InventoryDTO dto = new InventoryDTO();
            dto.setNumeroInv(inv.getNumeroInv());
            dto.setRemark(inv.getRemark());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<YardRowDTO> getParcLoges(String numeroInv) {
        List<ParcLoge> parcLoges = yardRowRepository.findByIdNumeroInv(numeroInv);
        if (parcLoges.isEmpty()) {
            throw new ResourceNotFoundException(
                "Aucun parc trouvé pour l'inventaire : " + numeroInv);
        }
        return parcLoges.stream().map(pl -> {
            YardRowDTO dto = new YardRowDTO();
            dto.setYard(pl.getId().getYard());
            dto.setRow(pl.getId().getRow());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public InventoryScanResponseDTO scannerBobine(InventoryScanRequestDTO request) {

        InventoryScanResponseDTO response = new InventoryScanResponseDTO();

        Optional<Coil> coilOpt = coilRepository.findById(request.getCoilSq());
        if (coilOpt.isEmpty()) {
            throw new ResourceNotFoundException(
                "Bobine introuvable : " + request.getCoilSq());
        }
        Coil coil = coilOpt.get();

        Optional<Inventory> invOpt = inventoryRepository.findById(request.getNumeroInv());
        if (invOpt.isEmpty() || invOpt.get().getClotureDt() != null) {
            throw new BusinessException(
                "Inventaire introuvable ou clôturé : " + request.getNumeroInv());
        }

        List<ParcLoge> parcLoges = yardRowRepository.findByIdNumeroInvAndIdYard(
            request.getNumeroInv(), request.getParcInv());
        boolean logeExiste = parcLoges.stream()
            .anyMatch(pl -> pl.getId().getRow().equals(request.getLogeInv()));
        if (!logeExiste) {
            throw new BusinessException(
                "Loge non trouvée dans l'inventaire : " + request.getLogeInv());
        }

        // ── Vérifier si bobine déjà scannée
        boolean dejaScanne = coilInventoryRepository
            .existsByIdCoilSqAndIdNumeroInv(
                request.getCoilSq(), request.getNumeroInv());

        // ── Message doublon via requête native
        if (dejaScanne) {
            List<Object[]> rows = entityManager.createNativeQuery("""
                SELECT CPARC, CLOGE, CNIVEAU, CPOSITION
                FROM UGFAB.T071COILS_INVENTAIRE
                WHERE CCOIL_SQ = :coilSq
                AND CNUMERO_INV = :numeroInv
                AND ROWNUM = 1
                """)
                .setParameter("coilSq", request.getCoilSq())
                .setParameter("numeroInv", request.getNumeroInv())
                .getResultList();

            if (!rows.isEmpty()) {
                Object[] row = rows.get(0);
                StringBuilder msgDoublon = new StringBuilder(
                    "Attention cette bobine a déjà été scannée " +
                    "dans cet inventaire à cet emplacement (");
                msgDoublon.append(row[0]);
                msgDoublon.append(".").append(row[1]);
                if (row[2] != null && !row[2].toString().isEmpty()) {
                    msgDoublon.append(".").append(row[2]);
                }
                if (row[3] != null && !row[3].toString().isEmpty()) {
                    msgDoublon.append(".").append(row[3]);
                }
                msgDoublon.append(")");
                response.setMessageDoublon(msgDoublon.toString());
            }
        }

        // ── Compter bobines dans la loge 
        
        List<Object> countRows = entityManager.createNativeQuery("""
            SELECT COUNT(*)
            FROM UGFAB.T071COILS_INVENTAIRE
            WHERE CNUMERO_INV = :numeroInv
            AND CPARC = :parc
            AND CLOGE = :loge
            """)
            .setParameter("numeroInv", request.getNumeroInv())
            .setParameter("parc", request.getParcInv())
            .setParameter("loge", request.getLogeInv())
            .getResultList();

        int nbCoils = countRows.isEmpty() ? 0 :
            ((Number) countRows.get(0)).intValue(); 

        // ── TOUJOURS INSERT
        insertCoilInventory(coil, request);
        if (!dejaScanne) {
            nbCoils++;
        }

        response.setSucces(true);
        response.setMessage(dejaScanne ?
            "Attention : bobine déjà scannée — enregistrement dupliqué" :
            "Bobine scannée avec succès");
        response.setDejaScanne(dejaScanne);
        response.setNbCoilsInv(nbCoils);
        response.setCoilId(coil.getTypeId() + coil.getCoilId() + coil.getCoupeId());
        response.setYard(request.getParcInv());
        response.setRow(request.getLogeInv());
        response.setPosition(request.getPosition());
        response.setLevel(request.getLevel());

        return response;
    }

    private void insertCoilInventory(Coil coil, InventoryScanRequestDTO request) {

        String gamme    = null;
        String commande = null;

        if (coil.getOrderSq() != null && coil.getPosteCde() != null) {
            Optional<PostOder> posteCmd = postOderRepository
                .findByIdCommandeSqAndIdPosteCde(
                    coil.getOrderSq(), coil.getPosteCde());
            if (posteCmd.isPresent()) {
                gamme    = posteCmd.get().getGamme();
                commande = posteCmd.get().getCommande();
            }
        }

        String parcInf = coil.getYard() != null ?
            coil.getYard().getYard() : null;
        String logeInf = coil.getLoge() != null ?
            coil.getLoge().getId().getRow() : null;

        entityManager.createNativeQuery("""
            INSERT INTO UGFAB.T071COILS_INVENTAIRE (
                CCOIL_SQ, CNUMERO_INV, CPARC, CLOGE,
                CPOSITION, CNIVEAU, CPARC_INF, CLOGE_INF,
                CNIVEAU_INF, CPOSITION_INF, CCOMMANDE_SQ,
                CPOSTE_CDE, CREMARQUE, CQUALITE, CLARGEUR,
                CEPAISSEUR, CPOIDS, CCHOIX, CGAMME,
                CCOMMANDE, CPRODUCTION_DT, CSCAN_DT,
                CINSERT_DT, CINSERT_NM, CUPDATE_DT,
                CUPDATE_NM, CFONCTION_NM
            ) VALUES (
                :coilSq, :numeroInv, :parc, :loge,
                :position, :niveau, :parcInf, :logeInf,
                :niveauInf, :positionInf, :commandeSq,
                :posteCde, :remarque, :qualite, :largeur,
                :epaisseur, :poids, :choix, :gamme,
                :commande, :productionDt, :scanDt,
                :insertDt, :insertNm, :updateDt,
                :updateNm, :fonctionNm
            )
            """)
            .setParameter("coilSq",       coil.getCoilSq())
            .setParameter("numeroInv",    request.getNumeroInv())
            .setParameter("parc",         request.getParcInv())
            .setParameter("loge",         request.getLogeInv())
            .setParameter("position",     request.getPosition())
            .setParameter("niveau",       request.getLevel())
            .setParameter("parcInf",      parcInf)
            .setParameter("logeInf",      logeInf)
            .setParameter("niveauInf",    request.getLevel())
            .setParameter("positionInf",  request.getPosition())
            .setParameter("commandeSq",   coil.getOrderSq())
            .setParameter("posteCde",     coil.getPosteCde())
            .setParameter("remarque",     coil.getRemark())
            .setParameter("qualite",      coil.getQuality())
            .setParameter("largeur",      coil.getWidth())
            .setParameter("epaisseur",    coil.getThickness())
            .setParameter("poids",        coil.getWeightNt())
            .setParameter("choix",        coil.getChoice())
            .setParameter("gamme",        gamme)
            .setParameter("commande",     commande)
            .setParameter("productionDt", coil.getProductionDt())
            .setParameter("scanDt",       LocalDateTime.now())
            .setParameter("insertDt",     LocalDateTime.now())
            .setParameter("insertNm",     "SCAN_INVENTAIRE")
            .setParameter("updateDt",     LocalDateTime.now())
            .setParameter("updateNm",     "SCAN_INVENTAIRE")
            .setParameter("fonctionNm",   "SCAN_INVENTAIRE")
            .executeUpdate();

        MyTools.logInfo("Inventaire — INSERT natif bobine : " + coil.getCoilId());
    }
}