package com.nlmk.LAL.MobileGuns.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nlmk.LAL.MobileGuns.entity.CoilMission;
import com.nlmk.LAL.MobileGuns.entity.CoilMissionId;

import jakarta.transaction.Transactional;

@Repository
public interface CoilMissionRepository extends JpaRepository<CoilMission, CoilMissionId> {

    // Toutes les missions d'une bobine
    List<CoilMission> findByIdCoilSq(Integer coilSq);

    // Mission ouverte d'une bobine
    @Query("SELECT cm FROM CoilMission cm WHERE cm.id.coilSq = :coilSq AND cm.endDt IS NULL")
    Optional<CoilMission> findMissionOuverteByCoilSq(@Param("coilSq") Integer coilSq);
    
    // Toutes les bobines d'une mission
    List<CoilMission> findByIdMissionSq(Integer missionSq);
    // Clôturer la mission DK1/DK2 (R6)
    @Modifying
    @Transactional
    @Query(value = "UPDATE UGFAB.T067COIL_MISSION " +
                   "SET CFIN_DT = SYSDATE, " +
                   "CLOGE_FIN = :loge, " +
                   "CNIVEAU_FIN = :niveau, " +
                   "CPOSITION_FIN = :position, " +
                   "CUPDATE_DT = SYSDATE, " +
                   "CUPDATE_NM = 'SCAN', " +
                   "CFONCTION_NM = 'SCAN_ENLOGEMENT' " +
                   "WHERE CMISSION_SQ = :missionSq " +
                   "AND CCOIL_SQ = :coilSq",
                   nativeQuery = true)
    void cloturerMission(@Param("missionSq") Integer missionSq,
                         @Param("coilSq") Integer coilSq,
                         @Param("loge") String loge,
                         @Param("niveau") String niveau,
                         @Param("position") String position);
}
