package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class DestinoFiltro {
    /** Busca en nombre y dirección */
    private String nombre;
    private String tipo;
    private Integer comuna;
    private Long cliente;
    private Boolean activo;
}
