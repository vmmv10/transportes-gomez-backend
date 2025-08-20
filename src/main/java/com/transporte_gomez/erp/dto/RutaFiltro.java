package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class RutaFiltro {

    private Integer id;
    private String fechaDesde;
    private String fechaHasta;
    private String fecha;
    private Long chofer;
    private String estado;
    private Boolean conductor;
}
