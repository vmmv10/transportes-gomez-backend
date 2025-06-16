package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class OrdenServicioDetalle {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String creadoEn; // Assuming this is a string representation of LocalDateTime
    private Integer cantidad;

}
