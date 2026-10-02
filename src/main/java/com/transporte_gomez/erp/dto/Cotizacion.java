package com.transporte_gomez.erp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.transporte_gomez.erp.enums.CotizacionCanal;
import com.transporte_gomez.erp.enums.CotizacionEstado;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class Cotizacion {
    private Long id;
    private String codigo;
    private CotizacionCanal canal;
    private CotizacionEstado estado;
    private String nombre;
    private String empresa;
    private String email;
    private String telefono;
    private Long clienteId;
    private String clienteNombre;
    private Integer servicioTipoId;
    private String servicioTipoNombre;
    private String servicioTexto;
    private String origen;
    private String destino;
    /** Punto marcado en el mapa por el solicitante (solo lectura) */
    private java.math.BigDecimal origenLatitud;
    private java.math.BigDecimal origenLongitud;
    private java.math.BigDecimal destinoLatitud;
    private java.math.BigDecimal destinoLongitud;
    private Integer comunaOrigenId;
    private String comunaOrigenNombre;
    private Integer comunaDestinoId;
    private String comunaDestinoNombre;
    private String tipoCarga;
    private BigDecimal pesoKg;
    private BigDecimal cantidad;
    private Integer unidadMedidaId;
    private String unidadMedidaNombre;
    private String mensaje;
    private BigDecimal monto;
    private Long tarifaId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validaHasta;
    private String observaciones;
    private String usuarioNombre;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;
}
