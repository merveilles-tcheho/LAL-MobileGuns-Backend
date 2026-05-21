package com.nlmk.LAL.MobileGuns.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;

@Data
@Embeddable
public class ParcLogeId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "CNUMERO_INV")
	private String  numeroInv;

	@Column(name = "CPARC")
	private String yard;

	@Column(name = "CLOGE")
	private String row;
}