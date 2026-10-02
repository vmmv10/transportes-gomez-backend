package com.transporte_gomez.erp.entity;

import com.transporte_gomez.erp.enums.VehiculoEstado;
import com.transporte_gomez.erp.enums.VehiculoPropiedad;
import com.transporte_gomez.erp.enums.VehiculoTipo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Flota propia y lanchas arrendadas a terceros. */
@Getter
@Setter
@Entity
@Table(name = "vehiculos")
public class VehiculoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private VehiculoTipo tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "propiedad", nullable = false, length = 10)
    private VehiculoPropiedad propiedad = VehiculoPropiedad.PROPIO;

    @Column(name = "patente", length = 20)
    private String patente;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "marca")
    private String marca;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "anio")
    private Integer anio;

    /** Arrendador (lanchas y vehículos arrendados) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private ProveedorEntity proveedor;

    @Column(name = "capacidad_kg", precision = 10, scale = 2)
    private BigDecimal capacidadKg;

    @Column(name = "capacidad_m3", precision = 10, scale = 2)
    private BigDecimal capacidadM3;

    @Column(name = "capacidad_pasajeros")
    private Integer capacidadPasajeros;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private VehiculoEstado estado = VehiculoEstado.OPERATIVO;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private OffsetDateTime fechaCreacion;
}
