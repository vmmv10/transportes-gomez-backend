package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DevolucionDetalle {

    private Long id;
    private BigDecimal cantidad;
    private Item item;
}
