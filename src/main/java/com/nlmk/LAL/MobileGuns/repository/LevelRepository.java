package com.nlmk.LAL.MobileGuns.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nlmk.LAL.MobileGuns.entity.Level;
import com.nlmk.LAL.MobileGuns.entity.LevelId;


@Repository
public interface LevelRepository extends JpaRepository<Level, LevelId> {

    // Tous les niveaux d'un parc
    List<Level> findByIdYard(String yard);

    // Tous les niveaux d'une loge
    List<Level> findByIdYardAndIdRow(String yard, String row);
}