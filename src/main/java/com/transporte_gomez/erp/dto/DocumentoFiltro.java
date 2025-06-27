package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class DocumentoFiltro {

    private Long usuarioId;
    private Long numero;
    private Integer tipoCodigo;
    private Long escuela;
    private Long proveedor;
    private String tipoNombre;
    private String fechaCreacionDesde;
    private String fechaCreacionHasta;
    private Boolean asignado;

}
