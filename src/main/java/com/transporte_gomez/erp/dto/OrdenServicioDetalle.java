package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrdenServicioDetalle {

    private Long id;
    private String nombre;
    private String escuela;
    private BigDecimal cantidad;
    private SaldoBodega saldoBodega;
    private Long item;

}
