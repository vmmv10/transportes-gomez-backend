package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "bultos")
public class BultoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingreso_id", nullable = false)
    private IngresosEntity ingreso;

    /** N° de seguimiento del cliente o transportista (Starken, Kaiken...) */
    @Column(name = "codigo_externo")
    private String codigoExterno;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "peso_kg", precision = 10, scale = 2)
    private BigDecimal pesoKg;

    @Column(name = "volumen_m3", precision = 10, scale = 3)
    private BigDecimal volumenM3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destino_id")
    private DestinoEntity destino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_servicio_id")
    private OrdenServicioEntity ordenServicio;

    /** EN_BODEGA, EN_RUTA, ENTREGADO, DEVUELTO o RECHAZADO */
    @Column(name = "estado", nullable = false)
    private String estado = "EN_BODEGA";

    @Column(name = "fecha_entrega")
    private OffsetDateTime fechaEntrega;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private OffsetDateTime fechaCreacion;
}
