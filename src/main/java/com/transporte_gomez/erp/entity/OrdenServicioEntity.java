package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "ordenes_servicios", schema = "qa")
public class OrdenServicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha", columnDefinition = "timestamp with time zone")
    private ZonedDateTime fecha = ZonedDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escuela_id", nullable = false)
    private EscuelaEntity escuela;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private ProveedorEntity proveedor;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "imagen")
    private String imagen;

}
