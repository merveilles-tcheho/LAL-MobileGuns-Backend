package com.nlmk.LAL.MobileGuns.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "T911LOGE", schema = "UGFAB")
public class Row {

	@EmbeddedId
	private RowId id;

	// ── Relation vers Parc

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CPARC", insertable = false, updatable = false)
	private Yard yard;

	@Column(name = "CINSERT_NM", nullable = false)
	private String insertNm;

	@Column(name = "CUPDATE_DT", nullable = false)
	private LocalDateTime updateDt;

	@Column(name = "CUPDATE_NM", nullable = false)
	private String updateNm;

	@Column(name = "CFONCTION_NM", nullable = false)
	private String fonctionNm;

	// ── Relation inverse vers Level ────────────────
	@JsonIgnore
	@OneToMany(mappedBy = "row", fetch = FetchType.LAZY)
	private List<Level> levels;
}
