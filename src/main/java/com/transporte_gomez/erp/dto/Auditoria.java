package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class Auditoria {

    private Long usuario;
    private OffsetDateTime fecha;
    private String operacion;
    private Integer modulo;
    private Long entidad;
    private Long id;

}
