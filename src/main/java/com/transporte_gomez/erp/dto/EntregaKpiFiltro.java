package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class EntregaKpiFiltro {
    private String tipo; // "rango", "mensual", "trimestral", "semestral"

    private String fechaInicio;   // yyyy-MM-dd
    private String fechaFin;      // yyyy-MM-dd

    private String fechaReferencia; // para mensual/trimestral -> yyyy-MM-dd

    private Integer year;

    private Long escuelaId;
}
