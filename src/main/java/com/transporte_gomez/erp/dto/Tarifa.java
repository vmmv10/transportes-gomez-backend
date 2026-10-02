package com.transporte_gomez.erp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.transporte_gomez.erp.enums.TarifaPeriodo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Tarifa {
    private Long id;
    /** Vacío = tarifa general */
    private Long clienteId;
    private String clienteNombre;
    private Integer servicioTipoId;
    private String servicioTipoNombre;
    /** Vacías = cualquier comuna */
    private Integer comunaOrigenId;
    private String comunaOrigenNombre;
    private Integer comunaDestinoId;
    private String comunaDestinoNombre;
    private Integer unidadMedidaId;
    private String unidadMedidaNombre;
    private String unidadMedidaCodigo;
    private TarifaPeriodo periodo;
    private BigDecimal precio;
    private BigDecimal minimo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate vigenteDesde;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate vigenteHasta;
    private String observaciones;
    private Boolean activo;
    /** Calculado: activa y vigente hoy */
    private Boolean vigente;
}
