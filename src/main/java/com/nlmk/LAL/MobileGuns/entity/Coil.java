package com.nlmk.LAL.MobileGuns.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "T001COILS", schema = "UGFAB")
public class Coil {
    @Id
    @Column(name = "CCOIL_SQ")
    private Integer coilSq;
    @Column(name = "CTYPE_ID")
    private String typeId;
    @Column(name = "CCOIL_ID")
    private String coilId;
    @Column(name = "CCOUPE_ID")
    private String coupeId;
    @Column(name = "CEPAISSEUR")
    private Double thickness;
    @Column(name = "CLARGEUR")
    private Double width;
    @Column(name = "CPOIDS_NET")
    private Double weightNt;
    @Column(name = "CEMBALLAGE_DT")
    private LocalDate packagingDt;
    @Column(name = "CEMBALLAGE_STR_DT")
    private LocalDate packagingStrDt;
    @Column(name = "CSOLDE_DT")
    private LocalDate soldeDt;
    @Column(name = "CQUALITE")
    private String quality;
    @Column(name = "CCHOIX")
    private String choice;
    @Column(name = "CREMARQUE")
    private String remark;
    @Column(name = "CCOMMANDE_SQ")
    private String orderSq;
    @Column(name = "CPOSTE_CDE")
    private String posteCde;
    @Column(name = "CPOSITION_INVALIDE")
    private String positionInvalide;
    @Column(name = "CPILE")        
    private String cpile;
    @Column(name = "CLIT")         
    private String clit;
    @Column(name = "CENLOGE_DT")
    private LocalDate enlogeDt;
    @Column(name = "CPRODUCTION_DT")
    private LocalDate productionDt;
    @Column(name = "CINSERT_NM", nullable = false)
    private String insertNm;
    @Column(name = "CUPDATE_DT", nullable = false)
    private LocalDateTime updateDt;
    @Column(name = "CUPDATE_NM", nullable = false)
    private String updateNm;
    @Column(name = "CFONCTION_NM", nullable = false)
    private String functionNm;

    // Relation vers Parc :
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CPARC", referencedColumnName = "CPARC",
                insertable = false, updatable = false)
    private Yard yard;

    // Relation vers Loge :
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumns({
        @JoinColumn(name = "CPARC", referencedColumnName = "CPARC",
                    insertable = false, updatable = false),
        @JoinColumn(name = "CLOGE", referencedColumnName = "CLOGE",
                    insertable = false, updatable = false)
    })
    private Row loge;

    // Relation vers CoilMission :
    @OneToMany(mappedBy = "coil", fetch = FetchType.LAZY)
    private List<CoilMission> coilMissions;
}