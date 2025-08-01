package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class IngresosFiltro {
    private String fechaDesde;
    private String fechaHasta;
    private Long documento;
    private Long bodega;
}
