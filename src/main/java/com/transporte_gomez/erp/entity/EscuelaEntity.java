package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Entity
@Data
@Table(name = "escuelas", schema = "qa")
public class EscuelaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "email")
    private String email;

    @Column(name = "fecha_creacion", columnDefinition = "timestamp with time zone")
    private ZonedDateTime fechaCreacion = ZonedDateTime.now();

    @Column(name = "rbd", nullable = false)
    private String rbd;

    @Column(name = "director")
    private String director;

    @Column(name = "comuna")
    private String comuna;

    @Column(name = "latitud")
    private String latitud;

    @Column(name = "longitud")
    private String longitud;

    @Column(name = "sostenedor_rut")
    private String sostenedorRut;
}
