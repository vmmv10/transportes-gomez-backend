package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Kpi {

    private String nombre;
    private BigDecimal valor;
    private BigDecimal porcentaje;
    private String unidad;

}
