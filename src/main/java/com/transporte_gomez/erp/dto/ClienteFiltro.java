package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class ClienteFiltro {
    /** Busca en razón social y nombre corto */
    private String nombre;
    private String rut;
    private String sector;
    private Boolean activo;
}
