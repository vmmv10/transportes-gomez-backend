package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
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

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "imagen")
    private String imagen;

    @ColumnDefault("false")
    @Column(name = "entregado", nullable = false)
    private Boolean entregado = false;

    @OneToMany(mappedBy = "ordenServicio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrdenServicioDetalleEntity> detalles;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "documento", nullable = false)
    private DocumentoEntity documento;

    @ColumnDefault("false")
    @Column(name = "en_ruta")
    private Boolean enRuta;

    @Column(name = "fecha_entrega")
    private Instant fechaEntrega;

}
