package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.util.List;

@Data
public class IngresoConversacion {
    private Long id;
    private Integer ingreso;
    private List<IngresoMensaje> mensajes;
    private Boolean cerrado;
}
