package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.ZonedDateTime;
@Getter
@Setter
@Entity
@Table(name = "proveedores")
public class ProveedorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "email")
    private String email;

    @Column(name = "fecha_creacion", columnDefinition = "timestamp with time zone")
    private ZonedDateTime fechaCreacion = ZonedDateTime.now();

    @Column(name = "rut", nullable = false)
    private String rut;

    @Column(name = "representante")
    private String representante;

    @ColumnDefault("true")
    @Column(name = "activo", nullable = false)
    private Boolean activo = false;

}
