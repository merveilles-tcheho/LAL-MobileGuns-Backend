package com.nlmk.LAL.MobileGuns.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.nlmk.LAL.MobileGuns.entity.CoilInventory;
import com.nlmk.LAL.MobileGuns.entity.CoilInventoryId;

@Repository
public interface CoilInventoryRepository extends JpaRepository<CoilInventory, CoilInventoryId> {
    // Toutes les bobines d'un inventaire
    List<CoilInventory> findByIdNumeroInv(String numeroInv); 
    // Vérifier si une bobine est déjà scannée
    boolean existsByIdCoilSqAndIdNumeroInv(Integer coilSq, String numeroInv); 
}