package com.transporte_gomez.erp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "ordenes_servicios")
public class OrdenServicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha", columnDefinition = "timestamp with time zone")
    private ZonedDateTime fecha = ZonedDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escuela_id")
    private EscuelaEntity escuela;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "imagen")
    private String imagen;

    @ColumnDefault("false")
    @Column(name = "entregado", nullable = false)
    private Boolean entregado = false;

    @OneToMany(mappedBy = "ordenServicio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrdenServicioDetalleEntity> detalles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documento")
    private DocumentoEntity documento;

    @ColumnDefault("false")
    @Column(name = "en_ruta")
    private Boolean enRuta;

    @Column(name = "fecha_entrega")
    private Instant fechaEntrega;

    @Column(name = "bodega")
    private Long bodega;

    @Column(name = "documento_referencia", length = Integer.MAX_VALUE)
    private String documentoReferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria")
    private CategoriaEntity categoria;

    @OneToOne(mappedBy = "ordenServicio", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private EntregaEntity entrega;

    @Column(name = "ingreso")
    private Integer ingreso;

    /** Código público para seguir el envío en la landing; lo genera la base al crear la orden. */
    @org.hibernate.annotations.Generated
    @Column(name = "codigo_seguimiento", insertable = false, updatable = false, length = 12)
    private String codigoSeguimiento;

    /** Quién contrata / a quién se le cobra */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_tipo_id")
    private ServicioTipoEntity servicioTipo;

    /** Punto de entrega (escuela, persona, empresa...) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destino_id")
    private DestinoEntity destino;

    /** Proveedor de la mercadería (ej. Kaiken, ABSA) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private ProveedorEntity proveedor;

    /** Contrato o licitación bajo el que se presta el servicio */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id")
    private ContratoEntity contrato;

}
