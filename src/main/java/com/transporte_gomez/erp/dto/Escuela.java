package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Escuela {
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private ZonedDateTime fechaCreacion;
    private String rbd;
    private String director;
    private String comuna;
    private String latitud;
    private String longitud;
}
