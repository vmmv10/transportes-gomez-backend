package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "ingreso_conversaciones")
public class IngresoConversacionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ingreso_id", nullable = false)
    private IngresosEntity ingreso;

    @ColumnDefault("now()")
    @Column(name = "creada_en")
    private LocalDateTime creadaEn;

    @ColumnDefault("false")
    @Column(name = "cerrada")
    private Boolean cerrada;

}