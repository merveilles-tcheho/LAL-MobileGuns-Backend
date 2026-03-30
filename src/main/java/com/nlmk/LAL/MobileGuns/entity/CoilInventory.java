package com.nlmk.LAL.MobileGuns.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "T071COILS_INVENTAIRE", schema = "UGFAB")
public class CoilInventory {

	@EmbeddedId
	private CoilInventoryId id;

	// Relation vers ParcLoge :

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "CNUMERO_INV", referencedColumnName = "CNUMERO_INV", insertable = false, updatable = false),
			@JoinColumn(name = "CPARC", referencedColumnName = "CPARC", insertable = false, updatable = false),
			@JoinColumn(name = "CLOGE", referencedColumnName = "CLOGE", insertable = false, updatable = false) })
	private ParcLoge parcLoge;

	// Relation vers Inventaire :

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CNUMERO_INV", insertable = false, updatable = false)
	private Inventory inventory;

	@Column(name = "CPARC")
	private String yard;

	@Column(name = "CLOGE")
	private String row;

	@Column(name = "CPOSITION")
	private String position;

	@Column(name = "CNIVEAU")
	private String level;

	@Column(name = "CPARC_INF")
	private String parcInf;

	@Column(name = "CLOGE_INF")
	private String logeInf;

	@Column(name = "CNIVEAU_INF")
	private String niveauInf;

	@Column(name = "CPOSITION_INF")
	private String positionInf;

	@Column(name = "CSCAN_DT")
	private LocalDateTime scanDt;

	@Column(name = "CCOMMANDE_SQ")
	private String commandeSq;

	@Column(name = "CPOSTE_CDE")
	private String posteCde;

	@Column(name = "CREMARQUE")
	private String remarque;

	@Column(name = "CQUALITE")
	private String qualite;

	@Column(name = "CLARGEUR")
	private Double largeur;

	@Column(name = "CEPAISSEUR")
	private Double epaisseur;

	@Column(name = "CPOIDS")
	private Double poids;

	@Column(name = "CCHOIX")
	private String choix;

	@Column(name = "CGAMME")
	private String gamme;

	@Column(name = "CCOMMANDE")
	private String commande;

	@Column(name = "CPRODUCTION_DT")
	private LocalDate productionDt;

	@Column(name = "CINSERT_DT")
	private LocalDateTime insertDt;

	@Column(name = "CINSERT_NM", nullable = false)
	private String insertNm;

	@Column(name = "CUPDATE_DT", nullable = false)
	private LocalDateTime updateDt;

	@Column(name = "CUPDATE_NM", nullable = false)
	private String updateNm;

	@Column(name = "CFONCTION_NM", nullable = false)
	private String functionNm;
}