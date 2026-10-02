package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class ContratoFiltro {
    private Long cliente;
    /** Busca en código y nombre */
    private String texto;
    private Boolean activo;
    /** true = solo contratos vigentes hoy */
    private Boolean vigente;
}
