package com.nlmk.LAL.MobileGuns.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nlmk.LAL.MobileGuns.entity.Coil;

import jakarta.transaction.Transactional;

@Repository
public interface CoilRepository extends JpaRepository<Coil, Integer> {

    
	//  Trouver une bobine par typeId + coilId + coupeId
	
	Optional<Coil> findByTypeIdAndCoilIdAndCoupeId(
	    String typeId,
	    String coilId,
	    String coupeId
	);

    // Compter bobines sur une position (R5)
    @Query(value = "SELECT COUNT(*) FROM UGFAB.T001COILS " +
                   "WHERE CPARC = :parc AND CLOGE = :loge " +
                   "AND CPILE = :position AND CLIT = :niveau " +
                   "AND (CPOSITION_INVALIDE = 'N' " +
                   "OR CPOSITION_INVALIDE IS NULL)",
                   nativeQuery = true)
    Integer countBobinesPosition(@Param("parc") String parc,
                                 @Param("loge") String loge,
                                 @Param("position") String position,
                                 @Param("niveau") String niveau);

    // UPDATE position normale — site LAL (R4 cas 1)
    @Modifying
    @Transactional
    @Query(value = "UPDATE UGFAB.T001COILS " +
                   "SET CPARC = :parc, CLOGE = :loge, " +
                   "CPILE = :position, CLIT = :niveau, " +
                   "CPOSITION_INVALIDE = '', " +
                   "CENLOGE_DT = SYSDATE, " +
                   "CUPDATE_DT = SYSDATE, " +
                   "CUPDATE_NM = 'SCAN_ENLOGEMENT', " +
                   "CFONCTION_NM = 'VERIF_POSITION' " +
                   "WHERE CCOIL_SQ = :coilSq",
                   nativeQuery = true)
    void updatePosition(@Param("coilSq") Integer coilSq,
                        @Param("parc") String parc,
                        @Param("loge") String loge,
                        @Param("position") String position,
                        @Param("niveau") String niveau);

    // UPDATE position reset ship (R4 cas 2)
    @Modifying
    @Transactional
    @Query(value = "UPDATE UGFAB.T001COILS " +
                   "SET CPARC = :parc, CLOGE = :loge, " +
                   "CPILE = :position, CLIT = :niveau, " +
                   "CPOSITION_INVALIDE = '', " +
                   "CENLOGE_DT = SYSDATE, " +
                   "CCHARGEMENT_SQ = null, " +
                   "CENLEVEMENT_SQ = null, " +
                   "CEXPEDITION_DT = null, " +
                   "CSTOCK = null, " +
                   "CUPDATE_DT = SYSDATE, " +
                   "CUPDATE_NM = 'SCAN_ENLOGEMENT', " +
                   "CFONCTION_NM = 'VERIF_POSITION' " +
                   "WHERE CCOIL_SQ = :coilSq",
                   nativeQuery = true)
    void updatePositionResetShip(@Param("coilSq") Integer coilSq,
                                 @Param("parc") String parc,
                                 @Param("loge") String loge,
                                 @Param("position") String position,
                                 @Param("niveau") String niveau);

    // UPDATE invalider bobines existantes (R5)
    @Modifying
    @Transactional
    @Query(value = "UPDATE UGFAB.T001COILS " +
                   "SET CPOSITION_INVALIDE = 'Y', " +
                   "CUPDATE_DT = SYSDATE, " +
                   "CUPDATE_NM = 'SCAN_ENLOGEMENT', " +
                   "CFONCTION_NM = 'VERIF_POSITION' " +
                   "WHERE CPARC = :parc AND CLOGE = :loge " +
                   "AND CPILE = :position AND CLIT = :niveau",
                   nativeQuery = true)
    void updatePositionInvalide(@Param("parc") String parc,
                                @Param("loge") String loge,
                                @Param("position") String position,
                                @Param("niveau") String niveau);
 // UPDATE emballage avec date (conditionnement O — 1er emballage)
    @Modifying
    @Transactional
    @Query(value = "UPDATE UGFAB.T001COILS " +
                   "SET CEMBALLAGE_DT = SYSDATE, " +
                   "CEMBALLAGE_CD = :emballageCd, " +
                   "CPROTECTION_RIVE = :protectionRive, " +
                   "CFEUILLARD_RAD = :feuillardRad, " +
                   "CFEUILLARD_CIR = :feuillardCir, " +
                   "CUPDATE_DT = SYSDATE, " +
                   "CUPDATE_NM = 'SCAN_ENLOGEMENT', " +
                   "CFONCTION_NM = 'GUN_SKI_TDR' " +
                   "WHERE CCOIL_SQ = :coilSq",
                   nativeQuery = true)
    void updateEmballageAvecDate(
        @Param("coilSq") Integer coilSq,
        @Param("emballageCd") String emballageCd,
        @Param("protectionRive") String protectionRive,
        @Param("feuillardRad") String feuillardRad,
        @Param("feuillardCir") String feuillardCir);

    // UPDATE emballage null date (conditionnement N — déconditionnement)
    @Modifying
    @Transactional
    @Query(value = "UPDATE UGFAB.T001COILS " +
                   "SET CEMBALLAGE_DT = null, " +
                   "CEMBALLAGE_CD = :emballageCd, " +
                   "CPROTECTION_RIVE = :protectionRive, " +
                   "CFEUILLARD_RAD = :feuillardRad, " +
                   "CFEUILLARD_CIR = :feuillardCir, " +
                   "CUPDATE_DT = SYSDATE, " +
                   "CUPDATE_NM = 'SCAN_ENLOGEMENT', " +
                   "CFONCTION_NM = 'GUN_SKI_TDR' " +
                   "WHERE CCOIL_SQ = :coilSq",
                   nativeQuery = true)
    void updateEmballageNullDate(
        @Param("coilSq") Integer coilSq,
        @Param("emballageCd") String emballageCd,
        @Param("protectionRive") String protectionRive,
        @Param("feuillardRad") String feuillardRad,
        @Param("feuillardCir") String feuillardCir);

    // UPDATE emballage sans date (conditionnement O — déjà emballé)
    @Modifying
    @Transactional
    @Query(value = "UPDATE UGFAB.T001COILS " +
                   "SET CEMBALLAGE_CD = :emballageCd, " +
                   "CPROTECTION_RIVE = :protectionRive, " +
                   "CFEUILLARD_RAD = :feuillardRad, " +
                   "CFEUILLARD_CIR = :feuillardCir, " +
                   "CUPDATE_DT = SYSDATE, " +
                   "CUPDATE_NM = 'SCAN_ENLOGEMENT', " +
                   "CFONCTION_NM = 'GUN_SKI_TDR' " +
                   "WHERE CCOIL_SQ = :coilSq",
                   nativeQuery = true)
    void updateEmballageSansDate(
        @Param("coilSq") Integer coilSq,
        @Param("emballageCd") String emballageCd,
        @Param("protectionRive") String protectionRive,
        @Param("feuillardRad") String feuillardRad,
        @Param("feuillardCir") String feuillardCir);
}

