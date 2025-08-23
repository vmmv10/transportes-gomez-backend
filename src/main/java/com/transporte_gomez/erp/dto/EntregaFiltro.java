package com.transporte_gomez.erp.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class EntregaFiltro {

    private Boolean entregado;
    private Long ordenServicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    private Integer id;
    private Long escuela;
    private Integer size;
    private Boolean conductor;
    private Long chofer;
}
