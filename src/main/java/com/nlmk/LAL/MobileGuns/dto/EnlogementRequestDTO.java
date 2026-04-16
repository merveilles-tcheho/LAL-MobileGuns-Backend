package com.nlmk.LAL.MobileGuns.dto;

import lombok.Data;

@Data
public class EnlogementRequestDTO {

	// Bobine à enloger
	private Integer coilSq;

	// Destination
	private String parcDestination;
	private String logeDestination;
	private String position;
	private String niveau;

	// Mode
	private String modeD;

	// Conditionnement (R2)

	private String conditionnement; // "O" ou "N"

	// Parc de session (choisi au menu)
	private String parcIni;

	// Forçage (R5)

	private boolean forcer;
	// ── Champs conditionnement (R2) ────────────────
	private String emballageCd;
	private String protectionRive;
	private String feuillardRad;
	private String feuillardCir;
}