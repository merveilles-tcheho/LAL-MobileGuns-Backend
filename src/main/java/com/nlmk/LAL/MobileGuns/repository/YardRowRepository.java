package com.nlmk.LAL.MobileGuns.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nlmk.LAL.MobileGuns.entity.ParcLoge;
import com.nlmk.LAL.MobileGuns.entity.ParcLogeId;

@Repository
public interface YardRowRepository extends JpaRepository<ParcLoge, ParcLogeId> {

    // Toutes les loges d'un inventaire
    List<ParcLoge> findByIdNumeroInv(Integer numeroInv);

    // Toutes les loges d'un inventaire dans un parc
    List<ParcLoge> findByIdNumeroInvAndIdYard(Integer numeroInv, String yard);
}
