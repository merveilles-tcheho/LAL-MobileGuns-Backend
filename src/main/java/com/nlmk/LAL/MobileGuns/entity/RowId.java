package com.nlmk.LAL.MobileGuns.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class RowId implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name = "CPARC")
	private String yard;

	@Column(name = "CLOGE")
	private String row;

}
