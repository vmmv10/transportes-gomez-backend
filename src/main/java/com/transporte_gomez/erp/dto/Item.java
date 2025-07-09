package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class Item {

    private Long id;
    private String nombre;
    private String descripcion;
    private UnidadMedida unidadMedida;
    private String codigo;

}
