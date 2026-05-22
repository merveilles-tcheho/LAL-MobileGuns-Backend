package com.nlmk.LAL.MobileGuns.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Table(name = "T_GUN_LAL_MOV_LOG", schema = "UGFAB")
public class MovLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "mov_log_seq")
    @SequenceGenerator(name = "mov_log_seq",
                       sequenceName = "UGFAB.SEQ_GUN_LAL_MOV_LOG",
                       allocationSize = 1)
    @Column(name = "CLOG_SQ")
    private Long logSq;

    @Column(name = "CCOIL_SQ")
    private Integer coilSq;

    @Column(name = "CTYPE_ID")
    private String typeId;

    @Column(name = "CCOIL_ID")
    private String coilId;

    @Column(name = "CCOUPE_ID")
    private String coupeId;

    @Column(name = "CPARC_FROM")
    private String parcFrom;

    @Column(name = "CLOGE_FROM")
    private String logeFrom;

    @Column(name = "CPILE_FROM")
    private String pileFrom;

    @Column(name = "CLIT_FROM")
    private Integer litFrom;

    @Column(name = "CPARC_TO")
    private String parcTo;

    @Column(name = "CLOGE_TO")
    private String logeTo;

    @Column(name = "CPILE_TO")
    private String pileTo;

    @Column(name = "CLIT_TO")
    private Integer litTo;

    @Column(name = "CINSERT_NM")
    private String insertNm;

    @Column(name = "CINSERT_DT")
    @JdbcTypeCode(SqlTypes.TIMESTAMP) 
    private LocalDateTime insertDt;

    @Column(name = "CUPDATE_NM")
    private String updateNm;

    @Column(name = "CUPDATE_DT")
    @JdbcTypeCode(SqlTypes.TIMESTAMP) 
    private LocalDateTime updateDt;

    @Column(name = "CFONCTION_NM")
    private String fonctionNm;
}