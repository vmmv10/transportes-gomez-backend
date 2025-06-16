package com.transporte_gomez.erp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "empresa", schema = "qa")
public class Empresa {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "razon_social", nullable = false, length = Integer.MAX_VALUE)
    private String razonSocial;

    @Column(name = "rut", nullable = false, length = Integer.MAX_VALUE)
    private String rut;

    @Column(name = "direccion", nullable = false, length = Integer.MAX_VALUE)
    private String direccion;

}