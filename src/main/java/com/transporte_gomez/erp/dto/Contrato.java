package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Contrato {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    /** ID de licitación u OC en Mercado Público */
    private String codigo;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String observaciones;
    private Boolean activo;
    /** Calculado: activo y dentro de sus fechas hoy */
    private Boolean vigente;
}
