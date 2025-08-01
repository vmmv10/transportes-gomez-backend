package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.util.List;

@Data
public class Item {

    private Long id;
    private String nombre;
    private String descripcion;
    private UnidadMedida unidadMedida;
    private String codigo;
    private Marca marca;
    private Categoria categoria;
    private List<ItemCodigoProveedor> codigosProveedor;

}
