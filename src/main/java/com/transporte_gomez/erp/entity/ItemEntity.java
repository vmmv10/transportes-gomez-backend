package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "item")
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, length = Integer.MAX_VALUE)
    private String nombre;

    @Column(name = "descripcion", length = Integer.MAX_VALUE)
    private String descripcion;

    @Column(name = "precio", precision = 10, scale = 2)
    private BigDecimal precio;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "creado_en")
    private Instant creadoEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_medida")
    private UnidadesMedidaEntity unidadMedida;

    @ColumnDefault("true")
    @Column(name = "activo", nullable = false)
    private Boolean activo = false;

    @Column(name = "codigo", length = 20)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria")
    private CategoriaEntity categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca")
    private MarcaEntity marca;

    @OneToMany( mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private java.util.List<ItemCodigoProveedorEntity> codigosProveedor;

}