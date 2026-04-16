package com.nlmk.LAL.MobileGuns.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nlmk.LAL.MobileGuns.dto.InventoryDTO;
import com.nlmk.LAL.MobileGuns.dto.InventoryScanRequestDTO;
import com.nlmk.LAL.MobileGuns.dto.InventoryScanResponseDTO;
import com.nlmk.LAL.MobileGuns.dto.YardRowDTO;
import com.nlmk.LAL.MobileGuns.entity.Coil;
import com.nlmk.LAL.MobileGuns.entity.CoilInventory;
import com.nlmk.LAL.MobileGuns.entity.CoilInventoryId;
import com.nlmk.LAL.MobileGuns.entity.Inventory;
import com.nlmk.LAL.MobileGuns.entity.ParcLoge;
import com.nlmk.LAL.MobileGuns.entity.PostOder;
import com.nlmk.LAL.MobileGuns.error.BusinessException;
import com.nlmk.LAL.MobileGuns.error.ResourceNotFoundException;
import com.nlmk.LAL.MobileGuns.repository.CoilInventoryRepository;
import com.nlmk.LAL.MobileGuns.repository.CoilRepository;
import com.nlmk.LAL.MobileGuns.repository.InventoryRepository;
import com.nlmk.LAL.MobileGuns.repository.PostOderRepository;
import com.nlmk.LAL.MobileGuns.repository.YardRowRepository;
import com.nlmk.LAL.MobileGuns.tools.MyTools;
import jakarta.transaction.Transactional;

@Service
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private YardRowRepository yardRowRepository;

	@Autowired
	private CoilRepository coilRepository;

	@Autowired
	private CoilInventoryRepository coilInventoryRepository;

	@Autowired
	private PostOderRepository postOderRepository;

	

	// ── Étape 1 — Liste inventaires ouverts

	@Override
	public List<InventoryDTO> getInventairesOuverts() {

		List<Inventory> inventaires = inventoryRepository.findByClotureDtIsNull();

		return inventaires.stream().map(inv -> {
			InventoryDTO dto = new InventoryDTO();
			dto.setNumeroInv(inv.getNumeroInv());
			dto.setRemark(inv.getRemark());
			return dto;
		}).collect(Collectors.toList());
	}

	// ── Étape 2 — Parcs d'un inventaire

	@Override
	public List<YardRowDTO> getParcLoges(Integer numeroInv) {

		List<ParcLoge> parcLoges = yardRowRepository.findByIdNumeroInv(numeroInv);

		if (parcLoges.isEmpty()) {
			throw new ResourceNotFoundException("Aucun parc trouvé pour l'inventaire : " + numeroInv);
		}

		return parcLoges.stream().map(pl -> {
			YardRowDTO dto = new YardRowDTO();
			dto.setYard(pl.getId().getYard());
			dto.setRow(pl.getId().getRow());
			return dto;
		}).collect(Collectors.toList());
	}

	// ── Étape 3 — Scanner une bobine

	@Override
	@Transactional
	public InventoryScanResponseDTO scannerBobine(InventoryScanRequestDTO request) {

		InventoryScanResponseDTO response = new InventoryScanResponseDTO();

		// ── Vérifier bobine existe dans T001COILS

		Optional<Coil> coilOpt = coilRepository.findById(request.getCoilSq());

		if (coilOpt.isEmpty()) {
			throw new ResourceNotFoundException("Bobine introuvable : " + request.getCoilSq());
		}

		Coil coil = coilOpt.get();

		// ── Vérifier inventaire existe et est ouvert

		Optional<Inventory> invOpt = inventoryRepository.findById(request.getNumeroInv());

		if (invOpt.isEmpty() || invOpt.get().getClotureDt() != null) {
			throw new BusinessException("Inventaire introuvable ou clôturé : " + request.getNumeroInv());
		}

		// ── Vérifier loge dans T072PARCS_LOGES_INVENTAIRE ─────
		List<ParcLoge> parcLoges = yardRowRepository.findByIdNumeroInvAndIdYard(request.getNumeroInv(),
				request.getParcInv());

		boolean logeExiste = parcLoges.stream().anyMatch(pl -> pl.getId().getRow().equals(request.getLogeInv()));

		if (!logeExiste) {
			throw new BusinessException("Loge non trouvée dans l'inventaire : " + request.getLogeInv());
		}

		// ── Vérifier si bobine déjà scannée

		boolean dejaScanne = coilInventoryRepository.existsByIdCoilSqAndIdNumeroInv(request.getCoilSq(),
				request.getNumeroInv());

		// ── Compter bobines déjà dans la loge

		List<CoilInventory> coilsInv = coilInventoryRepository.findByIdNumeroInv(request.getNumeroInv()).stream()
				.filter(ci -> request.getParcInv().equals(ci.getYard()) && request.getLogeInv().equals(ci.getRow()))
				.collect(Collectors.toList());

		int nbCoils = coilsInv.size();

		// ── INSERT ou UPDATE T071COILS_INVENTAIRE

		if (dejaScanne) {
			updateCoilInventory(coil, request);
			MyTools.logInfo("Inventaire — UPDATE bobine : " + coil.getCoilId());
		} else {
			insertCoilInventory(coil, request);
			nbCoils++;
			MyTools.logInfo("Inventaire — INSERT bobine : " + coil.getCoilId());
		}

		// ── Réponse

		response.setSucces(true);
		response.setMessage(dejaScanne ? "Bobine mise à jour" : "Bobine scannée avec succès");
		response.setDejaScanne(dejaScanne);
		response.setNbCoilsInv(nbCoils);
		response.setCoilId(coil.getCoilId());
		response.setYard(request.getParcInv());
		response.setRow(request.getLogeInv());
		response.setPosition(request.getPosition());
		response.setLevel(request.getLevel());

		return response;
	}
	// ── Méthode privée — INSERT T071COILS_INVENTAIRE ──────────
	
	private void insertCoilInventory(Coil coil,
	        InventoryScanRequestDTO request) {

	    // Récupérer gamme et commande via vue
		
	    String gamme    = null;
	    String commande = null;

	    if (coil.getOrderSq() != null &&
	        coil.getPosteCde() != null) {

	        Optional<PostOder> posteCmd = postOderRepository
	            .findByIdCommandeSqAndIdPosteCde(
	                coil.getOrderSq(),
	                coil.getPosteCde()
	            );

	        if (posteCmd.isPresent()) {
	            gamme    = posteCmd.get().getGamme();
	            commande = posteCmd.get().getCommande();
	        }
	    }

	    // Position actuelle de la bobine
	    
	    String parcInf = coil.getYard() != null ?
	        coil.getYard().getYard() : null;
	    String logeInf = coil.getLoge() != null ?
	        coil.getLoge().getId().getRow() : null;

	    // Créer l'entité
	    
	    CoilInventory ci = new CoilInventory();

	    // Clé composée
	    CoilInventoryId id = new CoilInventoryId();
	    id.setCoilSq(coil.getCoilSq());
	    id.setNumeroInv(request.getNumeroInv());
	    ci.setId(id);

	    // Position scannée
	    ci.setYard(request.getParcInv());
	    ci.setRow(request.getLogeInv());
	    ci.setLevel(request.getLevel());
	    ci.setPosition(request.getPosition());

	    // Position originale
	    ci.setParcInf(parcInf);
	    ci.setLogeInf(logeInf);
	    ci.setNiveauInf(request.getLevel());
	    ci.setPositionInf(request.getPosition());

	    // Infos bobine
	    ci.setCommandeSq(coil.getOrderSq());
	    ci.setPosteCde(coil.getPosteCde());
	    ci.setRemarque(coil.getRemark());
	    ci.setQualite(coil.getQuality());
	    ci.setLargeur(coil.getWidth());
	    ci.setEpaisseur(coil.getThickness());
	    ci.setPoids(coil.getWeightNt());
	    ci.setChoix(coil.getChoice());
	    ci.setProductionDt(coil.getProductionDt());

	    // Gamme et commande
	    ci.setGamme(gamme);
	    ci.setCommande(commande);

	    // Dates et audit
	    ci.setScanDt(LocalDateTime.now());
	    ci.setInsertDt(LocalDateTime.now());
	    ci.setInsertNm("SCAN_INVENTAIRE");
	    ci.setUpdateDt(LocalDateTime.now());
	    ci.setUpdateNm("SCAN_INVENTAIRE");
	    ci.setFunctionNm("SCAN_INVENTAIRE");

	    // Sauvegarder
	    coilInventoryRepository.save(ci);

	    MyTools.logInfo("Inventaire — INSERT bobine : "
	        + coil.getCoilId());
	}

	// ── Méthode privée — UPDATE T071COILS_INVENTAIRE
	
	private void updateCoilInventory(Coil coil,
	        InventoryScanRequestDTO request) {

	    // Récupérer l'entité existante
	    CoilInventoryId id = new CoilInventoryId();
	    id.setCoilSq(coil.getCoilSq());
	    id.setNumeroInv(request.getNumeroInv());

	    CoilInventory ci = coilInventoryRepository
	        .findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException(
	            "CoilInventory introuvable"));

	    // Mettre à jour la position
	    ci.setYard(request.getParcInv());
	    ci.setRow(request.getLogeInv());
	    ci.setLevel(request.getLevel());
	    ci.setPosition(request.getPosition());
	    ci.setScanDt(LocalDateTime.now());
	    ci.setUpdateDt(LocalDateTime.now());
	    ci.setUpdateNm("SCAN_INVENTAIRE");
	    ci.setFunctionNm("SCAN_INVENTAIRE");

	    // Sauvegarder
	    coilInventoryRepository.save(ci);

	    MyTools.logInfo("Inventaire — UPDATE bobine : "
	        + coil.getCoilId());
	}

	}