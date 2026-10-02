package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "servicios_tipos")
public class ServicioTipoEntity {

    public static final String CARGA_TERRESTRE = "CARGA_TER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "codigo", nullable = false)
    private String codigo;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    /** CARGA, PASAJEROS o ALMACENAJE */
    @Column(name = "categoria", nullable = false)
    private String categoria;

    /** TERRESTRE, MARITIMO o null (almacenaje) */
    @Column(name = "modalidad")
    private String modalidad;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}
