package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class RutaCostoFiltro {
    /** yyyy-MM-dd */
    private String desde;
    /** yyyy-MM-dd */
    private String hasta;
    private Long vehiculo;
    private Long chofer;
    /** true = solo rutas que tienen al menos un costo */
    private Boolean conCostos;
}
