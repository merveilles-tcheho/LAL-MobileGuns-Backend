package com.nlmk.LAL.MobileGuns.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nlmk.LAL.MobileGuns.entity.MovLog;

@Repository
public interface MovLogRepository 
    extends JpaRepository<MovLog, Long> {

    //  Vérifier si bobine déjà déplacée
    List<MovLog> findByCoilSqOrderByInsertDtDesc(Integer coilSq);
}