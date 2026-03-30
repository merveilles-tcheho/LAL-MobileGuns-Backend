package com.nlmk.LAL.MobileGuns.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "T070INVENTAIRES", schema = "UGFAB")
public class Inventory {

	@Id
	@Column(name = "CNUMERO_INV")
	private Integer numeroInv;

	@Column(name = "CREMARQUE")
	private String remark;

	@Column(name = "CDEBUT_DT")
	private LocalDateTime debutDt;

	@Column(name = "CCLOTURE_DT")
	private LocalDateTime clotureDt;

	@Column(name = "CINSERT_DT")
	private LocalDateTime insertDt;

	@Column(name = "CINSERT_NM", nullable = false)
	private String insertNm;

	@Column(name = "CUPDATE_DT", nullable = false)
	private LocalDateTime updateDt;

	@Column(name = "CUPDATE_NM", nullable = false)
	private String updateNm;

	@Column(name = "CFONCTION_NM", nullable = false)
	private String fonctionNm;

	// ── Relation inverse vers ParcLoge

	@JsonIgnore
	@OneToMany(mappedBy = "inventory", fetch = FetchType.LAZY)
	private List<ParcLoge> parcLoges;

	// ── Relation inverse vers CoilInventaire

	@JsonIgnore
	@OneToMany(mappedBy = "inventory", fetch = FetchType.LAZY)
	private List<CoilInventory> coilInventory;
}