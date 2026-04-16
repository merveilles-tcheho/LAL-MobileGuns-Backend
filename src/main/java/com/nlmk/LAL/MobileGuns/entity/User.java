package com.nlmk.LAL.MobileGuns.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "TUT005_USR", schema = "UTILITIES")
@Data
public class User {

	@Id
	@Column(name = "CUT005_USER_ID")
	private String userId;

	@Column(name = "CUT005_USER_ID_SQ")
	private Integer userSq;

	@Column(name = "CUT005_USER_NAME")
	private String userName;

	@Column(name = "CUT005_EMAIL")
	private String email;

	@Column(name = "CUT005_ACTIVE")
	private String isActive;

	@Transient
	private String password;

	@Transient
	private List<String> roles;

}

