package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class EscuelaFilter {
    private String nombre;
    private String comuna;
    private String rbd;
    private String director;
    private Boolean activo;
    /** id del cliente (el usuario Cliente solo ve los suyos) */
    private Long cliente;
}
