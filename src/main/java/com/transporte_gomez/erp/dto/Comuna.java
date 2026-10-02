package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class Comuna {
    private Integer id;
    private String codigoIne;
    private String nombre;
    private String provincia;
    private String region;
    private Boolean activo;
}
