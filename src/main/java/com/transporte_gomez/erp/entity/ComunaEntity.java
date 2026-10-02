package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "comunas")
public class ComunaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "codigo_ine", nullable = false, length = 5)
    private String codigoIne;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "provincia", nullable = false)
    private String provincia;

    @Column(name = "region", nullable = false)
    private String region;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}
