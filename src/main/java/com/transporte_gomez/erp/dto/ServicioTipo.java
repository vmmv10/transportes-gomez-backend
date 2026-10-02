package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class ServicioTipo {
    private Integer id;
    private String codigo;
    private String nombre;
    private String categoria;
    private String modalidad;
    private Boolean activo;
}
