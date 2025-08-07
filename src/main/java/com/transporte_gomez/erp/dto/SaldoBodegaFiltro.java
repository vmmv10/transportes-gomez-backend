package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaldoBodegaFiltro {
    private Integer bodega;
    private Long itemId;
    private String nombre;
    private Integer marca;
    private Integer categoria;
    private BigDecimal saldoBodega;
    private String codigo;
}
