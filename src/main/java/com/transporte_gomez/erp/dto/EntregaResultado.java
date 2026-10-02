package com.transporte_gomez.erp.dto;

import com.transporte_gomez.erp.enums.EntregaEstado;
import lombok.Data;

import java.math.BigDecimal;

/** Lo que envía el conductor al no poder entregar, o la señal de proximidad. */
@Data
public class EntregaResultado {
    /** NO_ENTREGADO o RECHAZADO */
    private EntregaEstado estado;
    /** "Establecimiento cerrado", "Nadie para recibir", ... */
    private String motivo;
    private BigDecimal latitud;
    private BigDecimal longitud;
    /** Solo proximidad */
    private BigDecimal distanciaKm;
}
