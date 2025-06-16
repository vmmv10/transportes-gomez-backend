package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class OrdenServicioFiltro {
    private Long id;
    private String fecha;
    private Long escuelaId;
    private Long proveedorId;
    private Long documentoId;
    private Boolean enRuta;
}
