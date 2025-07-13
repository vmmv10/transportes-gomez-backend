package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "ingresos_emergencia", schema = "qa")
public class IngresosEmergencia {
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

}