package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class IngresosEmergenciaFiltro {
    private String fechaDesde;
    private String fechaHasta;
    private Long documento;
}
