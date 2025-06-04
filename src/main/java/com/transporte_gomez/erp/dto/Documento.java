package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class Documento {

    private Long id;
    private Usuario usuario;
    private Long numero;
    private Integer tipoCodigo;
    private String tipoNombre;
    private Proveedor proveedor;
}
