package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class Usuario {

    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private String telefono;
    private String apellidos;
    private Boolean modoOscuro;
}
