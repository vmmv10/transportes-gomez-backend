package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "entregas", schema = "qa")
public class EntregaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ruta_id", nullable = false)
    private RutaEntity ruta;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orden_servicio_id", nullable = false)
    private OrdenServicioEntity ordenServicio;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "creado_en")
    private Instant creadoEn;

    @ColumnDefault("false")
    @Column(name = "entregado", nullable = false)
    private Boolean entregado = false;

    @Column(name = "orden", nullable = false)
    private Integer orden;

}