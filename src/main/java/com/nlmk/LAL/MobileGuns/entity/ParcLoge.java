package com.nlmk.LAL.MobileGuns.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "T072PARCS_LOGES_INVENTAIRE")
public class ParcLoge {

	@EmbeddedId
    private ParcLogeId id;

    // ── Relation vers Inventory ────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "CNUMERO_INV",
        insertable = false,
        updatable = false
    )
    private Inventory inventory;

    // ── Relation inverse vers CoilInventory ───────
    @JsonIgnore
    @OneToMany(mappedBy = "parcLoge", fetch = FetchType.LAZY)
    private List<CoilInventory> coilInventory;

    // ── Colonnes ───────────────────────────────────
    @Column(name = "CCLOTURE_DT")
    private LocalDateTime clotureDt;

    @Column(name = "CAJOUT_COIL")
    private String ajoutCoil;
}