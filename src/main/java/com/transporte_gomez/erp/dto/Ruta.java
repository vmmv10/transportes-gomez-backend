package com.transporte_gomez.erp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class Ruta {
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    private Usuario chofer;
    private String estado;
    private List<OrdenServicio> ordenes;
    private List<Entrega> entregas;
    private Integer orden;
    private Boolean enTransito;
    private Instant inicio;
    private Instant fin;
    /** Kilómetros recorridos. Si hay odómetro de salida y llegada, se calcula con ellos. */
    private Integer kilometros;
    /** Vacío = "Sin vehículo" */
    private Vehiculo vehiculo;
    private Integer kmSalida;
    private Integer kmLlegada;
    /** Suma de los costos de la ruta (solo lectura) */
    private BigDecimal costoTotal;
}
