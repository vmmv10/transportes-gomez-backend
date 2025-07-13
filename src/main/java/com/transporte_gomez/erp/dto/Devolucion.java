package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.util.List;

@Data
public class Devolucion {
    private Long id;
    private String motivo;
    private String fecha;
    private Integer estado;
    private String estadoNombre;
    private OrdenServicio ordenServicio;
    private Escuela escuela;
    private List<DevolucionDetalle> detalles;
}
