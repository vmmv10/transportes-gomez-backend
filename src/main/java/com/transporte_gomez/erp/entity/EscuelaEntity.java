package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(name = "rbd_provincia")
    private Integer rbdProvincia;

    @Column(name = "tipo", length = Integer.MAX_VALUE)
    private String tipo;

    @Column(name = "red_fija", length = Integer.MAX_VALUE)
    private String redFija;

    @ColumnDefault("false")
    @Column(name = "activo", nullable = false)
    private Boolean activo = false;

}
