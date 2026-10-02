package com.transporte_gomez.erp.dto;

import com.transporte_gomez.erp.enums.VehiculoEstado;
import com.transporte_gomez.erp.enums.VehiculoPropiedad;
import com.transporte_gomez.erp.enums.VehiculoTipo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Vehiculo {
    private Long id;
    private VehiculoTipo tipo;
    private VehiculoPropiedad propiedad;
    private String patente;
    private String nombre;
    private String marca;
    private String modelo;
    private Integer anio;
    /** Arrendador */
    private Long proveedorId;
    private String proveedorNombre;
    private BigDecimal capacidadKg;
    private BigDecimal capacidadM3;
    private Integer capacidadPasajeros;
    private VehiculoEstado estado;
    private String observaciones;
    private Boolean activo;
    /** Calculado: "Hilux blanca · ABCD12" */
    private String descripcion;
}
