package com.nlmk.LAL.MobileGuns.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nlmk.LAL.MobileGuns.entity.Coil;

import jakarta.transaction.Transactional;

@Repository
public interface CoilInconnuRepository extends JpaRepository<Coil, Integer> {

    // DELETE T068COILS_INCONNU après enlogement
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM UGFAB.T068COILS_INCONNU " +
                   "WHERE CPARC = :parc AND CLOGE = :loge " +
                   "AND CPILE = :position AND CLIT = :niveau",
                   nativeQuery = true)
    void deleteByPosition(@Param("parc") String parc,
                          @Param("loge") String loge,
                          @Param("position") String position,
                          @Param("niveau") String niveau);}