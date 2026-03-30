package com.nlmk.LAL.MobileGuns.repository;

import com.nlmk.LAL.MobileGuns.entity.Position;
import com.nlmk.LAL.MobileGuns.entity.PositionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, PositionId> {

    // Toutes les positions d'une loge
    List<Position> findByIdYardAndIdRow(String yard, String row);

    // Positions filtrées par statut
    List<Position> findByIdYardAndIdRowAndStatus(String yard, String row, String status);
}