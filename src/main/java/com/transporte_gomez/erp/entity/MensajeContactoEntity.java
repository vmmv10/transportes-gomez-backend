package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/** Mensaje del formulario de contacto de la landing. */
@Getter
@Setter
@Entity
@Table(name = "mensajes_contacto")
public class MensajeContactoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo", nullable = false, length = 20)
    private com.transporte_gomez.erp.enums.MensajeMotivo motivo = com.transporte_gomez.erp.enums.MensajeMotivo.CONSULTA;

    /** Si el motivo es el estado de un envío */
    @Column(name = "codigo_seguimiento", length = 12)
    private String codigoSeguimiento;

    @Column(name = "atendido", nullable = false)
    private Boolean atendido = false;

    @Column(name = "ip", length = 45)
    private String ip;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();
}
