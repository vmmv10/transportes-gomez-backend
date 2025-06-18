package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EntregaFiltro {

    private Boolean entregado;
    private Long ordenServicio;
    private LocalDate fecha;
    private Integer id;
}
