package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "documentos", schema = "qa")
public class DocumentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @Column(name = "numero")
    private Long numero;

    @Column(name = "tipo_codigo")
    private Integer tipoCodigo;

    @Column(name = "tipo_nombre", length = Integer.MAX_VALUE)
    private String tipoNombre;

    @Column(name = "imagen")
    private Long imagen;

    @ColumnDefault("now()")
    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor")
    private ProveedorEntity proveedor;

}