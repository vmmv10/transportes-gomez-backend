package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

/** Tarifa que calza con la cotización y el monto que resulta. */
@Data
public class TarifaSugerida {
    private Tarifa tarifa;
    /** max(precio × cantidad, mínimo), redondeado a pesos */
    private BigDecimal monto;
    private String detalle;
}
