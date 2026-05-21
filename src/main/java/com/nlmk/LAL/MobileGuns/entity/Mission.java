package com.nlmk.LAL.MobileGuns.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "T066MISSIONS", schema = "UGFAB")
public class Mission {

    @Id
    @Column(name = "CMISSION_SQ")
    private Integer missionSq;

    @Column(name = "CMISSION_TYPE_CD")
    private String missionTypeCd;

    @Column(name = "CLIBELLE")
    private String libelle;

    @Column(name = "CENVOI_DT")
    private LocalDateTime envoiDt;

    @Column(name = "CCOMMENTAIRE")
    private String commentaire;

    @Column(name = "CPARC")
    private String parc;

    @Column(name = "CDESTINATION")
    private String destination;

    @Column(name = "CPRIORITE")
    private Integer priorite;

    @Column(name = "CINSERT_DT")
    private LocalDateTime insertDt;

    @Column(name = "CINSERT_NM")
    private String insertNm;

    @Column(name = "CUPDATE_DT")
    private LocalDateTime updateDt;

    @Column(name = "CUPDATE_NM")
    private String updateNm;

    @Column(name = "CFONCTION_NM")
    private String functionNm;

    @Column(name = "CCONFIRMATION_DT")
    private LocalDateTime confirmationDt;

   
    // ── Relation inverse vers CoilMission
    @OneToMany(mappedBy = "mission", fetch = FetchType.LAZY)
    private List<CoilMission> coilMissions;
}