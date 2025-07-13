package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "ingresos_emergencia_detalles", schema = "qa")
public class IngresosEmergenciaDetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ingreso")
    private IngresosEmergencia ingreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "items")
    private ItemEntity items;

    @Column(name = "cantidad", nullable = false)
    private BigDecimal cantidad;

}