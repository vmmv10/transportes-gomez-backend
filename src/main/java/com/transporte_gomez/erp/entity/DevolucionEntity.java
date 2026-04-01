package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "devoluciones")
public class DevolucionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escuela_id")
    private EscuelaEntity escuela;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id")
    private OrdenServicioEntity orden;

    @ColumnDefault("now()")
    @Column(name = "fecha")
    private Instant fecha;

    @Column(name = "estado")
    private Integer estado;

    @Column(name = "motivo", length = Integer.MAX_VALUE)
    private String motivo;

    @OneToMany(mappedBy = "devolucion")
    private List<DevolucionDetalleEntity> devolucionesDetalles = new ArrayList<>();

    @Column(name = "\"user\"")
    private Long user;

}