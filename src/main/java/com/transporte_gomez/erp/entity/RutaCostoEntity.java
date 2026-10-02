package com.transporte_gomez.erp.entity;

import com.transporte_gomez.erp.enums.RutaCostoTipo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/** Gasto imputado a una ruta: combustible, peajes, cruces, arriendo de lancha, viáticos. */
@Getter
@Setter
@Entity
@Table(name = "rutas_costos")
public class RutaCostoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ruta_id", nullable = false)
    private RutaEntity ruta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private RutaCostoTipo tipo;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    /** Pesos chilenos */
    @Column(name = "monto", nullable = false, precision = 12, scale = 0)
    private BigDecimal monto;

    @Column(name = "litros", precision = 10, scale = 2)
    private BigDecimal litros;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private ProveedorEntity proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id")
    private VehiculoEntity vehiculo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "comprobante")
    private String comprobante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private OffsetDateTime fechaCreacion;
}
