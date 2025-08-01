package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IngresosDetalle {

    private Integer id;
    private BigDecimal cantidad;
    private Item item;
}
