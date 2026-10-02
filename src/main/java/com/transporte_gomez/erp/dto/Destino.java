package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class Destino {
    private Long id;
    /** ESCUELA, JARDIN, OFICINA, PERSONA, EMPRESA u OTRO */
    private String tipo;
    private String nombre;
    private String direccion;
    private Comuna comuna;
    private String latitud;
    private String longitud;
    private String contacto;
    private String telefono;
    private String email;
    private Long escuelaId;
    private Long clienteId;
    private String clienteNombre;
    private Boolean activo;
}
