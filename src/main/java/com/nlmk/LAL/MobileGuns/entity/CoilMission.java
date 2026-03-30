package com.nlmk.LAL.MobileGuns.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "T067COIL_MISSION" , schema = "UGFAB")

public class CoilMission {

	@EmbeddedId
	private CoilMissionId id;

	// ── Relation vers Mission

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CMISSION_SQ", insertable = false, updatable = false)
	private Mission mission;

	// ── Relation vers Coil

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCOIL_SQ", insertable = false, updatable = false)
	private Coil coil;

	// ── Colonnes

	@Column(name = "CORDRE")
	private Integer order;

	@Column(name = "CFIN_DT")
	private LocalDateTime endDt;

	@Column(name = "CINSERT_DT")
	private LocalDateTime insertDt;

	@Column(name = "CINSERT_NM")
	private String insertNm;

	@Column(name = "CUPDATE_DT")
	private LocalDateTime updateDt;

	@Column(name = "CUPDATE_NM")
	private String updateNm;

	@Column(name = "CFONCTION_NM")
	private String fonctionNm;

}
