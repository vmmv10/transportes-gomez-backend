package com.transporte_gomez.erp.entity;

import com.transporte_gomez.erp.enums.TarifaPeriodo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/** Precio por servicio, comunas, unidad y período. Sin cliente = tarifa general. */
@Getter
@Setter
@Entity
@Table(name = "tarifas")
public class TarifaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servicio_tipo_id", nullable = false)
    private ServicioTipoEntity servicioTipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comuna_origen_id")
    private ComunaEntity comunaOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comuna_destino_id")
    private ComunaEntity comunaDestino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidad_medida_id", nullable = false)
    private UnidadesMedidaEntity unidadMedida;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo", nullable = false, length = 10)
    private TarifaPeriodo periodo = TarifaPeriodo.VIAJE;

    @Column(name = "precio", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(name = "minimo", precision = 12, scale = 2)
    private BigDecimal minimo;

    @Column(name = "vigente_desde", nullable = false)
    private LocalDate vigenteDesde;

    @Column(name = "vigente_hasta")
    private LocalDate vigenteHasta;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    public boolean isVigente(LocalDate fecha) {
        return Boolean.TRUE.equals(activo)
                && !fecha.isBefore(vigenteDesde)
                && (vigenteHasta == null || !fecha.isAfter(vigenteHasta));
    }
}
