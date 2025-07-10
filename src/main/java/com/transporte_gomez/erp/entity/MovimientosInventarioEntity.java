package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "movimientos_inventario", schema = "qa")
public class MovimientosInventarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "tipo_movimiento", length = 10)
    private String tipoMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_id")
    private BodegaEntity bodega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    @Column(name = "cantidad", nullable = false)
    private BigDecimal cantidad;

    @ColumnDefault("now()")
    @Column(name = "fecha")
    private Instant fecha;

    @Column(name = "tipo")
    private Integer tipo;

    @Column(name = "observaciones", length = Integer.MAX_VALUE)
    private String observaciones;

}