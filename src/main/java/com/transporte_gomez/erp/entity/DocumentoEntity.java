package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "documentos")
public class DocumentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @Column(name = "imagen")
    private Long imagen;

    @ColumnDefault("now()")
    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor")
    private ProveedorEntity proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escuela")
    private EscuelaEntity escuela;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo")
    private DocumentoTipoEntity tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bodega", nullable = false)
    private BodegaEntity bodega;

    @ColumnDefault("false")
    @Column(name = "entregado", nullable = false)
    private Boolean entregado = false;

    @ColumnDefault("false")
    @Column(name = "asignado", nullable = false)
    private Boolean asignado = false;

    @Column(name = "numero", nullable = false, length = Integer.MAX_VALUE)
    private String numero;

}