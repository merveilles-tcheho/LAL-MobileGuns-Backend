package com.nlmk.LAL.MobileGuns.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "T066MISSIONS", schema = "UGFAB")
public class Mission {

	@Id
	@Column(name = "CMISSION_SQ")
	private Integer missionSq;

	@Column(name = "CDESTINATION")
	private String destination;

	@Column(name = "CLIBELLE")
	private String libelle;

	@Column(name = "CCREATION_DT")
	private LocalDate creationDt;

	@Column(name = "CLOTURE_DT")
	private LocalDate clotureDt; // ← corrigé

	@Column(name = "CINSERT_NM")
	private String insertNm;

	@Column(name = "CUPDATE_DT")
	private LocalDate updateDt;

	@Column(name = "CUPDATE_NM")
	private String updateNm;

	@Column(name = "CFUNCTION_NM")
	private String functionNm;

	// ── Relation inverse vers CoilMission ──────────

	@OneToMany(mappedBy = "mission", fetch = FetchType.LAZY)
	private List<CoilMission> coilMissions;
}