package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "auditoria", schema = "qa")
public class AuditoriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "operacion", nullable = false, length = Integer.MAX_VALUE)
    private String operacion;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "modulo_id")
    private Integer moduloId;

    @ColumnDefault("now()")
    @Column(name = "fecha")
    private OffsetDateTime fecha;

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;

}