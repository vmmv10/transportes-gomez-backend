package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ingresos_emergencia", schema = "qa")
public class IngresosEmergenciaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ColumnDefault("now()")
    @Column(name = "fecha")
    private Instant fecha;

    @Column(name = "documento")
    private Long documento;

    @Column(name = "observaciones", length = Integer.MAX_VALUE)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento_tipo")
    private DocumentoTipoEntity documentoTipo;

    @OneToMany(mappedBy = "ingreso", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<IngresosEmergenciaDetalleEntity> detalles;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bodega", nullable = false)
    private BodegaEntity bodega;

    @Column(name = "estado", nullable = false)
    private Integer estado;

    @Column(name = "\"user\"")
    private Long user;

}