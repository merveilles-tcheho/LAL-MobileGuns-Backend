package com.nlmk.LAL.MobileGuns.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "T910PARC", schema = "UGFAB")
public class Yard {
	@Id
	@Column(name = "CPARC")
	private String yard;

	@Column(name = "CLIBELLE")
	private String libelle;

	@Column(name = "CTYPE")
	private String type;

	@Column(name = "CDISPONIBILITE")
	private String disponibilite;

	@Column(name = "CDIMENSION")
	private String dimension;

	@Column(name = "CINSERT_NM")
	private String insertNm;

	@Column(name = "CUPDATE_DT")
	private LocalDate updateDt;

	@Column(name = "CUPDATE_NM")
	private String updateNm;

}
