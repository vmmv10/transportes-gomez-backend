package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class CategoriaFiltro {

    private Integer id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
}
