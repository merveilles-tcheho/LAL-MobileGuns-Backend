package com.nlmk.LAL.MobileGuns.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class CoilMissionId implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "CMISSION_SQ")
	private Integer missionSq;

	@Column(name = "CCOIL_SQ")
	private Integer coilSq;

}
