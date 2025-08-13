package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class EntregaDashboard {
    private Long entregasHoy;
    private Long entregasRealizadas;
    private Long entregasTotal;
    private Long entregasPendientes;

}
