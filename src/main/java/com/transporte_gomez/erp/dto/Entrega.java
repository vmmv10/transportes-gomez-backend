package com.transporte_gomez.erp.dto;

import lombok.Data;

import com.transporte_gomez.erp.enums.EntregaEstado;

import java.time.LocalDate;

@Data
public class Entrega {

    private Integer id;
    private OrdenServicio ordenServicio;
    private boolean entregado;
    private LocalDate fecha;
    private Integer ruta;
    private Integer orden;
    /** PENDIENTE, ENTREGADO, NO_ENTREGADO, RECHAZADO */
    private EntregaEstado estado;
    private String motivo;
    private Integer intentos;
}
