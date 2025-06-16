package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Ruta {
    private Integer id;
    private LocalDate fecha;
    private Usuario chofer;
    private String estado;
}
