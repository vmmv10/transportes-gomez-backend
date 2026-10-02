package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class Bulto {
    private Long id;
    private Integer ingresoId;
    /** N° de seguimiento del cliente o transportista */
    private String codigoExterno;
    private String descripcion;
    private BigDecimal pesoKg;
    private BigDecimal volumenM3;
    private Destino destino;
    private Long ordenServicioId;
    /** EN_BODEGA, EN_RUTA, ENTREGADO, DEVUELTO o RECHAZADO */
    private String estado;
    private OffsetDateTime fechaEntrega;
}
