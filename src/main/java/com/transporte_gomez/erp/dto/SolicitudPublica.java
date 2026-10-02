package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Lo que envía la landing: solicitud de cotización o mensaje de contacto.
 * "sitioWeb" es un campo trampa invisible: si viene con texto, es un robot.
 * "captcha" es el token de Cloudflare Turnstile (si está configurado).
 */
@Data
public class SolicitudPublica {
    private String nombre;
    private String empresa;
    private String email;
    private String telefono;
    /** terrestre, maritimo, almacenaje (slug de la landing, versión anterior) */
    private String servicio;
    /** Código de servicios_tipos (CARGA_TER, CARGA_MAR, ALMAC...). Tiene prioridad sobre "servicio". */
    private String servicioCodigo;
    /** Nombre del servicio tal como lo muestra la landing */
    private String servicioTexto;
    private String origen;
    private String destino;
    private Integer comunaOrigenId;
    private Integer comunaDestinoId;
    /** Punto marcado en el mapa (opcional) */
    private BigDecimal origenLatitud;
    private BigDecimal origenLongitud;
    private BigDecimal destinoLatitud;
    private BigDecimal destinoLongitud;
    private String tipoCarga;
    private BigDecimal pesoKg;
    private String mensaje;
    /** Contacto: CONSULTA, ENVIO, RECLAMO, PROVEEDOR, TRABAJO o PRECIO */
    private String motivo;
    /** Contacto: código de seguimiento si pregunta por un envío */
    private String codigoSeguimiento;
    private String sitioWeb;
    private String captcha;
}
