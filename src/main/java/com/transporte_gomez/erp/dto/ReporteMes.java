package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class ReporteMes {

    private String mes;
    private Long total;

    public ReporteMes(String mes, Long total) {
        this.mes = mes;
        this.total = total;
    }
}
