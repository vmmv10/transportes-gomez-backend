package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "clientes")
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "rut", nullable = false)
    private String rut;

    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    @Column(name = "nombre_corto")
    private String nombreCorto;

    /** NATURAL o JURIDICA */
    @Column(name = "tipo_persona", nullable = false)
    private String tipoPersona = "JURIDICA";

    /** PUBLICO o PRIVADO */
    @Column(name = "sector", nullable = false)
    private String sector = "PRIVADO";

    @Column(name = "giro")
    private String giro;

    @Column(name = "direccion")
    private String direccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comuna_id")
    private ComunaEntity comuna;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "email")
    private String email;

    @Column(name = "contacto")
    private String contacto;

    /** Código de unidad compradora en Mercado Público */
    @Column(name = "codigo_mp")
    private String codigoMp;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private OffsetDateTime fechaCreacion;
}
