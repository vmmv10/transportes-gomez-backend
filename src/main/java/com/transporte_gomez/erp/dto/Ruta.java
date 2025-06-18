package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Ruta {
    private Integer id;
    private LocalDate fecha;
    private Usuario chofer;
    private String estado;
    private List<OrdenServicio> ordenes;
    private List<Entrega> entregas;
}
