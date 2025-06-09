package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Auditoria {

    private String usuario;
    private LocalDate fecha;
    private String operacion;
    private String modulo;
    private Long id;

}
