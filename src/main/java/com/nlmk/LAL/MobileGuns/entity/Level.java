package com.nlmk.LAL.MobileGuns.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "T912NIVEAU", schema = "UGFAB")
public class Level {

	@EmbeddedId
	private LevelId id;

	// ── Relation vers Loge :

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "CPARC", referencedColumnName = "CPARC", insertable = false, updatable = false),
			@JoinColumn(name = "CLOGE", referencedColumnName = "CLOGE", insertable = false, updatable = false) })
	private Row row;

	// ── Colonnes
	@Column(name = "CINSERT_NM", nullable = false)
	private String insertNm;

	@Column(name = "CUPDATE_DT", nullable = false)
	private LocalDate updateDt;

	@Column(name = "CUPDATE_NM", nullable = false)
	private String updateNm;

	@Column(name = "CFONCTION_NM", nullable = false)
	private String fonctionNm;

	// ── Relation inverse vers Position

	@JsonIgnore
	@OneToMany(mappedBy = "level", fetch = FetchType.LAZY)
	private List<Position> positions;

}
