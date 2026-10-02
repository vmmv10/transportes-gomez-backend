package com.transporte_gomez.erp.entity;

import com.transporte_gomez.erp.enums.SeguimientoTipo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Evento del historial de una orden: en ruta, cerca del destino, entregada, no entregada... */
@Getter
@Setter
@Entity
@Table(name = "seguimiento_eventos")
public class SeguimientoEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orden_servicio_id", nullable = false)
    private OrdenServicioEntity ordenServicio;

    @Column(name = "entrega_id")
    private Integer entregaId;

    @Column(name = "ruta_id")
    private Integer rutaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private SeguimientoTipo tipo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "latitud", precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(name = "longitud", precision = 10, scale = 7)
    private BigDecimal longitud;

    @Column(name = "distancia_km", precision = 8, scale = 3)
    private BigDecimal distanciaKm;

    @Column(name = "visible_publico", nullable = false)
    private Boolean visiblePublico = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @Column(name = "fecha", nullable = false)
    private OffsetDateTime fecha = OffsetDateTime.now();
}
