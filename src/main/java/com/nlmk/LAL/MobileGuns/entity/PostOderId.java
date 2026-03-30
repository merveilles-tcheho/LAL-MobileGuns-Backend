package com.nlmk.LAL.MobileGuns.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class PostOderId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "CCOMMANDE_SQ")
    private String commandeSq;

    @Column(name = "CPOSTE_CDE")
    private String posteCde;
}