package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class MarcaFiltro {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
}
