package com.transporte_gomez.erp.dto;

import com.transporte_gomez.erp.enums.SeguimientoTipo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class SeguimientoEvento {
    private Long id;
    private Long ordenServicioId;
    private Integer entregaId;
    private Integer rutaId;
    private SeguimientoTipo tipo;
    private String descripcion;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private BigDecimal distanciaKm;
    private Boolean visiblePublico;
    private String usuarioNombre;
    private OffsetDateTime fecha;
}
