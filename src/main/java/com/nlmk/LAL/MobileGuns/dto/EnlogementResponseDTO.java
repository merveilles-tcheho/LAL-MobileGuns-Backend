package com.nlmk.LAL.MobileGuns.dto;

import lombok.Data;

@Data
public class EnlogementResponseDTO {

    // ── Résultat ──────────────────────────────────
    private boolean succes;
    private String message;

    // ── Position occupée → forçage requis (R5) ────
    private boolean positionOccupee;
    private Integer nombreBobinesPresentes;

    // ── Infos de la bobine enlogée ────────────────
    private String coilId;
    private String parcFinal;
    private String logeFinal;
    private String positionFinal;
    private String niveauFinal;

    // ──  Alerte doublon d'étiquette ─────────────
    private boolean dejaDeplace;
    private String messageAlerte;
}