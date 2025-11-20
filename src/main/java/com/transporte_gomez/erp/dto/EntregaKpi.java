package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class EntregaKpi {
    private Long escuelaId;
    private String escuelaNombre;

    private Long entregasRealizadas;
    private Long entregasPlanificadas;

    private Double kpi;

    public EntregaKpi(long l, long l1, double v) {
    }
}
