package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class IngresoMensaje {
    private Long id;
    private String mensaje;
    private Usuario usuario;
    private String fecha;
    private Boolean esSistema;
}
