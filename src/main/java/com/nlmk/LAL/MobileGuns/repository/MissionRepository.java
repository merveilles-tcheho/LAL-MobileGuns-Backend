package com.nlmk.LAL.MobileGuns.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.nlmk.LAL.MobileGuns.entity.Mission;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Integer> {
    List<Mission> findByDestination(String destination);
}