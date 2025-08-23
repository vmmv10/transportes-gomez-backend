package com.transporte_gomez.erp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class Ruta {
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    private Usuario chofer;
    private String estado;
    private List<OrdenServicio> ordenes;
    private List<Entrega> entregas;
    private Integer orden;
    private Boolean enTransito;
    private Instant inicio;
    private Instant fin;
    private Integer kilometros;
}
