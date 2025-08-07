package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SaldoBodega {
    private Integer id;
    private Item item;
    private BigDecimal saldo;
    private BigDecimal cantidad;
}
