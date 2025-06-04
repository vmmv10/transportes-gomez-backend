package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class Proveedor {

    private Long id;
    private String nombre;
    private String rut;
    private String direccion;
    private String telefono;
    private String email;
    private String contacto;
    private boolean activo;

}
