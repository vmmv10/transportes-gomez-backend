package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

/** Lo que ve cualquier persona con el código de seguimiento: sin datos internos. */
@Data
public class SeguimientoPublico {
    private String codigo;
    /** RECIBIDA, EN_RUTA, ENTREGADA, NO_ENTREGADA */
    private String estado;
    private String estadoTexto;
    private String destino;
    private String comuna;
    private OffsetDateTime fechaEntrega;
    private List<Evento> eventos;

    @Data
    public static class Evento {
        private String tipo;
        private String titulo;
        private OffsetDateTime fecha;
    }
}
