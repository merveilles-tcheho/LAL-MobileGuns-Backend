package com.nlmk.LAL.MobileGuns.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nlmk.LAL.MobileGuns.dto.IdentificationDTO;
import com.nlmk.LAL.MobileGuns.entity.Coil;
import com.nlmk.LAL.MobileGuns.entity.PostOder;
import com.nlmk.LAL.MobileGuns.error.ResourceNotFoundException;
import com.nlmk.LAL.MobileGuns.repository.CoilRepository;
import com.nlmk.LAL.MobileGuns.repository.PostOderRepository;

@Service
public class IdentificationServiceImpl implements IdentificationService {

    @Autowired
    private CoilRepository coilRepository;

    @Autowired
    private PostOderRepository postOderRepository;  

    @Override
    public IdentificationDTO identifier(
            String typeId, String coilId, String coupeId) {

        // Chercher par les 3 champs
        Optional<Coil> result = coilRepository
            .findByTypeIdAndCoilIdAndCoupeId(
                typeId, coilId, coupeId
            );

        if (result.isEmpty()) {
            throw new ResourceNotFoundException(
                "Bobine introuvable : " +
                typeId + "/" + coilId + "/" + coupeId
            );
        }

        Coil coil = result.get();
        IdentificationDTO dto = new IdentificationDTO();

        dto.setCoilSq(coil.getCoilSq());
        dto.setCoilId(coil.getCoilId());
        dto.setCoupeId(coil.getCoupeId());
        dto.setTypeId(coil.getTypeId());
        dto.setParc(coil.getYard() != null ?
            coil.getYard().getYard() : null);
        dto.setLoge(coil.getLoge() != null ?
            coil.getLoge().getId().getRow() : null);
        dto.setEpaisseur(coil.getThickness());
        dto.setLargeur(coil.getWidth());
        dto.setPoidsNet(coil.getWeightNt());
        dto.setPackagingDt(coil.getPackagingDt());
        dto.setPackagingStrDt(coil.getPackagingStrDt());
        dto.setOrderSq(coil.getOrderSq());
        dto.setPosteCde(coil.getPosteCde());
        dto.setQuality(coil.getQuality());
        dto.setChoice(coil.getChoice());

        // ✅ CHAUD : typeId = 4 ou 6
        // ✅ FROID : typeId = 1, 2, 8, 9
        String t = typeId.trim();
        boolean isChaud = t.equals("4") || t.equals("6");
        dto.setTemperature(isChaud ? "CHAUD" : "FROID");

        // Soldé
        dto.setSolde(coil.getSoldeDt() != null);
        dto.setSoldeDt(coil.getSoldeDt() != null ?
            coil.getSoldeDt().toString() : null);

        // Commande et gamme
        if (coil.getOrderSq() != null &&
            coil.getPosteCde() != null) {
            Optional<PostOder> posteCmd = postOderRepository
                .findByIdCommandeSqAndIdPosteCde(
                    coil.getOrderSq(),
                    coil.getPosteCde()
                );
            if (posteCmd.isPresent()) {
                dto.setCommande(posteCmd.get().getCommande());
                dto.setGamme(posteCmd.get().getGamme());
            }
        }

        return dto;
    }
    }
