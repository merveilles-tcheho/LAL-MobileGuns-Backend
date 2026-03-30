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
    public IdentificationDTO identifier(String coilId) {

        // Chercher la bobine dans Oracle
        Optional<Coil> result = coilRepository.findByCoilId(coilId);

        // Règle 1 — bobine introuvable
        if (result.isEmpty()) {
            throw new ResourceNotFoundException(
                "Bobine introuvable : " + coilId);
        }

        Coil coil = result.get();
        IdentificationDTO dto = new IdentificationDTO();

        // Données de base
        dto.setCoilSq(coil.getCoilSq());
        dto.setCoilId(coil.getCoilId());
        dto.setCoupeId(coil.getCoupeId());
        dto.setTypeId(coil.getTypeId());
        dto.setParc(coil.getYard() != null ? coil.getYard().getYard() : null);
        dto.setLoge(coil.getLoge() != null ? coil.getLoge().getId().getRow() : null);
        dto.setEpaisseur(coil.getThickness());
        dto.setLargeur(coil.getWidth());
        dto.setPoidsNet(coil.getWeightNt());
        dto.setPackagingDt(coil.getPackagingDt());
        dto.setOrderSq(coil.getOrderSq());
        dto.setPosteCde(coil.getPosteCde());
        dto.setQuality(coil.getQuality());
        dto.setChoice(coil.getChoice());

        // Règle 2 — alerte SOLDÉ
        dto.setSolde(coil.getSoldeDt() != null);
        dto.setSoldeDt(
            coil.getSoldeDt() != null ? coil.getSoldeDt().toString() : null
        );

        // Règle 3 — chaud ou froid
        String typeId = coil.getTypeId();
        if (typeId != null && !typeId.isEmpty()) {
            char c = typeId.charAt(0);
            dto.setTemperature(c >= 'A' && c <= 'Z' ? "CHAUD" : "FROID");
        }

        // Vue V011POSTE_COMMANDE — commande et gamme
        if (coil.getOrderSq() != null && coil.getPosteCde() != null) {
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


