package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class OrdenServicio {
    private Long id;
    private OffsetDateTime fecha;
    private Escuela escuela;
    private String observaciones;
    private String imagen;
    private Documento documento;
    private List<OrdenServicioDetalle> detalles;
    private boolean entregado;
    private Bodega bodega;
    private String documentoReferencia;
    private Categoria categoria;
    private Integer ingreso;
}
