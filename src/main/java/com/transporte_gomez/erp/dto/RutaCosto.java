package com.transporte_gomez.erp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.transporte_gomez.erp.enums.RutaCostoTipo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RutaCosto {
    private Long id;
    private Integer rutaId;
    private RutaCostoTipo tipo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    private BigDecimal monto;
    private BigDecimal litros;
    private Long proveedorId;
    private String proveedorNombre;
    private Long vehiculoId;
    private String vehiculoDescripcion;
    private String descripcion;
    private String comprobante;
    private String usuarioNombre;
}
