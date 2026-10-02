package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class MensajeContacto {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String mensaje;
    private com.transporte_gomez.erp.enums.MensajeMotivo motivo;
    private String motivoTexto;
    private String codigoSeguimiento;
    private Boolean atendido;
    private OffsetDateTime fechaCreacion;
}
