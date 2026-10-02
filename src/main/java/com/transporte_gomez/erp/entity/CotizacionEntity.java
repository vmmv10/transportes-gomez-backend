package com.transporte_gomez.erp.entity;

import com.transporte_gomez.erp.enums.CotizacionCanal;
import com.transporte_gomez.erp.enums.CotizacionEstado;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/** Solicitud de cotización (landing) o cotización interna. */
@Getter
@Setter
@Entity
@Table(name = "cotizaciones")
public class CotizacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false, length = 10)
    private CotizacionCanal canal = CotizacionCanal.WEB;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private CotizacionEstado estado = CotizacionEstado.NUEVA;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "empresa")
    private String empresa;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "telefono")
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_tipo_id")
    private ServicioTipoEntity servicioTipo;

    @Column(name = "servicio_texto")
    private String servicioTexto;

    @Column(name = "origen")
    private String origen;

    @Column(name = "destino")
    private String destino;

    @Column(name = "origen_latitud", precision = 10, scale = 7)
    private java.math.BigDecimal origenLatitud;

    @Column(name = "origen_longitud", precision = 10, scale = 7)
    private java.math.BigDecimal origenLongitud;

    @Column(name = "destino_latitud", precision = 10, scale = 7)
    private java.math.BigDecimal destinoLatitud;

    @Column(name = "destino_longitud", precision = 10, scale = 7)
    private java.math.BigDecimal destinoLongitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comuna_origen_id")
    private ComunaEntity comunaOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comuna_destino_id")
    private ComunaEntity comunaDestino;

    @Column(name = "tipo_carga")
    private String tipoCarga;

    @Column(name = "peso_kg", precision = 12, scale = 2)
    private BigDecimal pesoKg;

    @Column(name = "cantidad", precision = 12, scale = 2)
    private BigDecimal cantidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidad_medida_id")
    private UnidadesMedidaEntity unidadMedida;

    @Column(name = "mensaje")
    private String mensaje;

    @Column(name = "monto", precision = 12, scale = 0)
    private BigDecimal monto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarifa_id")
    private TarifaEntity tarifa;

    @Column(name = "valida_hasta")
    private LocalDate validaHasta;

    @Column(name = "observaciones")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    @Column(name = "ip", length = 45)
    private String ip;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion = OffsetDateTime.now();

    @Column(name = "fecha_actualizacion")
    private OffsetDateTime fechaActualizacion;
}
