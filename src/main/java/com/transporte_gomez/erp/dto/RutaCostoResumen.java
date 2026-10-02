package com.transporte_gomez.erp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/** Costo real de una ruta: total, por tipo y por kilómetro. */
@Data
public class RutaCostoResumen {
    private Integer rutaId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    private String estado;
    private String chofer;
    private Long vehiculoId;
    /** "Sin vehículo" si la ruta no tiene */
    private String vehiculo;
    private Integer kilometros;
    private Integer entregas;
    private BigDecimal total;
    /** Monto por tipo de costo (COMBUSTIBLE, PEAJE, ...) */
    private Map<String, BigDecimal> porTipo;
    private BigDecimal litros;
    /** total / kilómetros; vacío si no hay kilómetros */
    private BigDecimal costoPorKm;
    /** total / entregas; vacío si no hay entregas */
    private BigDecimal costoPorEntrega;
}
