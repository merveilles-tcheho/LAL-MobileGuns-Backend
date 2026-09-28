package com.nlmk.LAL.MobileGuns.service;

import java.util.List;
import com.nlmk.LAL.MobileGuns.dto.DashboardInventaireDTO;

import com.nlmk.LAL.MobileGuns.dto.DashboardBobineDTO;
import com.nlmk.LAL.MobileGuns.dto.DashboardLogeDTO;
import com.nlmk.LAL.MobileGuns.dto.DashboardMouvementDTO;
import com.nlmk.LAL.MobileGuns.dto.DashboardParcDTO;
import com.nlmk.LAL.MobileGuns.dto.DashboardStatsDTO;

public interface DashboardService {

    DashboardStatsDTO getStats();

    List<DashboardParcDTO> getParcs();

    List<DashboardMouvementDTO> getDerniersMouvements();

    
    List<DashboardLogeDTO> getLogesByParc(String parc);

    List<DashboardBobineDTO> getBobinesByLoge(String parc, String loge);
    
    List<DashboardInventaireDTO> getInventaires();
}