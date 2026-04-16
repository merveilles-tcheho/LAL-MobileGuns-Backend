package com.nlmk.LAL.MobileGuns.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nlmk.LAL.MobileGuns.entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

	// Inventaires ouverts — pas encore clôturés
	List<Inventory> findByClotureDtIsNull();
}
