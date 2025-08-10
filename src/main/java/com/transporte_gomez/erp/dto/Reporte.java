package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class Reporte {

    private String titulo;
    private Long total;

    public Reporte(String titulo, Long total) {
        this.titulo = titulo;
        this.total = total;
    }
}
