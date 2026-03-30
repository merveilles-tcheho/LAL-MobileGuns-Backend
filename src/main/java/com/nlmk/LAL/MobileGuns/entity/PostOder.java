package com.nlmk.LAL.MobileGuns.entity;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Immutable                          // ← vue = lecture seule
@Table(name = "V011POSTE_COMMANDE", schema = "UGFAB")
public class PostOder {

    @EmbeddedId
    private PostOderId id;

    @Column(name = "CCOMMANDE")
    private String commande;

    @Column(name = "CGAMME")
    private String gamme;

    @Column(name = "CEMBALLAGE_CD")
    private String emballageCd;

    @Column(name = "CPROTECTION_RIVE")
    private String protectionRive;

    @Column(name = "CETIQUETTE")
    private String etiquette;

    @Column(name = "CFEUILLARD_RADIAUX")
    private String feuillardRadiaux;

    @Column(name = "CFEUILLARD_CIRCULAIRE")
    private String feuillardCirculaire;
}
