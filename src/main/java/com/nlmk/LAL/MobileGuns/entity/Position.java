package com.nlmk.LAL.MobileGuns.entity;

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
@Table(name = "T913POSITION", schema = "UGFAB")
public class Position {

	@EmbeddedId
	private PositionId id;

	// ── Relation vers Loge

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "CPARC", referencedColumnName = "CPARC", insertable = false, updatable = false),
			@JoinColumn(name = "CLOGE", referencedColumnName = "CLOGE", insertable = false, updatable = false) })
	private Row loge;

	// ── Relation vers Niveau

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "CPARC", referencedColumnName = "CPARC", insertable = false, updatable = false),
			@JoinColumn(name = "CLOGE", referencedColumnName = "CLOGE", insertable = false, updatable = false),
			@JoinColumn(name = "CNIVEAU", referencedColumnName = "CNIVEAU", insertable = false, updatable = false) })
	private Level level;

	@Column(name = "CSTATUS")
	private String status;

	@Column(name = "CINSERT_DT")
	private LocalDateTime insertDt;

	@Column(name = "CINSERT_NM")
	private String insertNm;

	@Column(name = "CUPDATE_DT")
	private LocalDateTime updateDt;

	@Column(name = "CUPDATE_NM")
	private String updateNm;

	@Column(name = "CFONCTION_NM")
	private String functionNm;
}
