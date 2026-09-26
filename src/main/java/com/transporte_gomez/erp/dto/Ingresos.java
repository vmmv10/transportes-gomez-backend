package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.util.List;

@Data
public class Ingresos {
    private Integer id;
    private String fecha;
    private String fechaCierre;
    private Long documento;
    private DocumentoTipo documentoTipo;
    private List<IngresosDetalle> detalles;
    private String observaciones;
    private Bodega bodega;
    private Integer estado;
    private String ordenCompra;
    /** Quién contrata / a quién se le cobra */
    private Cliente cliente;
    /** Quién trajo la carga a la bodega */
    private Proveedor transportista;
    private String guiaTransportista;
    /** Solo se llena al pedir el ingreso con detalles */
    private List<Bulto> bultos;
}
