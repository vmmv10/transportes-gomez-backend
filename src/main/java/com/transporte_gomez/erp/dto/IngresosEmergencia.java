package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.util.List;

@Data
public class IngresosEmergencia {
    private Integer id;
    private String fecha;
    private Long documento;
    private DocumentoTipo documentoTipo;
    private List<IngresosEmergenciaDetalle> detalles;
    private String observaciones;
    private Bodega bodega;
    private Integer estado;
}
