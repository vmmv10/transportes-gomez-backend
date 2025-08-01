package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class ItemCodigoProveedor {
    private Long id;
    private String codigo;
    private Proveedor proveedor;
}
