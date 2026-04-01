package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "imagenes")
public class ImagenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "entidad_tipo", nullable = false, length = 50)
    private Integer entidadTipo;

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;

    @Column(name = "nombre_original")
    private String nombreOriginal;

    @Column(name = "ruta", nullable = false, length = 500)
    private String ruta;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    @Column(name = "ruta_thumb", length = 500)
    private String rutaThumbnail;

}