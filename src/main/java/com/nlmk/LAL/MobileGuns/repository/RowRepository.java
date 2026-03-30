package com.nlmk.LAL.MobileGuns.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nlmk.LAL.MobileGuns.entity.Row;
import com.nlmk.LAL.MobileGuns.entity.RowId;


@Repository
public interface RowRepository extends JpaRepository<Row, RowId> {

    // Toutes les loges d'un parc
    List<Row> findByIdYard(String yard);
}
