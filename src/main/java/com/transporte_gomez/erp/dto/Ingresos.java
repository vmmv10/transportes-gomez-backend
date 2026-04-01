package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.util.List;

@Data
public class Ingresos {
    private Integer id;
    private String fecha;
    private String fechaCierre;
    private Long documento;
    private DocumentoTipo documentoTipo;
    private List<IngresosDetalle> detalles;
    private String observaciones;
    private Bodega bodega;
    private Integer estado;
    private String ordenCompra;
}
