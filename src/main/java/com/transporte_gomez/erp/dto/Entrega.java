package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Entrega {

    private Integer id;
    private OrdenServicio ordenServicio;
    private boolean entregado;
    private LocalDate fecha;
    private Integer ruta;
    private Integer orden;
}
