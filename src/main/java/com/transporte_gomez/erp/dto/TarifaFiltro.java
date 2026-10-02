package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class TarifaFiltro {
    /** -1 = solo tarifas generales */
    private Long cliente;
    private Integer servicio;
    private Integer comuna;
    private Boolean activo;
    /** true = solo vigentes hoy */
    private Boolean vigente;
}
