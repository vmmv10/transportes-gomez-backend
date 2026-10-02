package com.transporte_gomez.erp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Devuelve los errores de validación como 400 con un mensaje legible,
 * para que el frontend pueda mostrarlo (ej. "RUT inválido", "Ya existe un cliente con RUT ...").
 * Antes llegaban como 500 sin mensaje.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.transporte_gomez.erp.controller")
public class ErroresHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> validacion(IllegalArgumentException ex) {
        log.warn("Solicitud rechazada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "status", 400,
                "message", ex.getMessage() != null ? ex.getMessage() : "Solicitud inválida",
                "timestamp", OffsetDateTime.now().toString()));
    }

    /** Registro de otra organización (usuario Cliente) u operación sin permiso. */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> sinAcceso(AccessDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "status", 403,
                "message", ex.getMessage() != null ? ex.getMessage() : "No tienes acceso a este registro",
                "timestamp", OffsetDateTime.now().toString()));
    }

    /** Formularios públicos: demasiados envíos desde la misma IP. */
    @ExceptionHandler(com.transporte_gomez.erp.config.ProteccionPublica.DemasiadasSolicitudesException.class)
    public ResponseEntity<Map<String, Object>> demasiadas(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                "status", 429,
                "message", ex.getMessage(),
                "timestamp", OffsetDateTime.now().toString()));
    }

    /** Problemas de configuración o de un servicio externo (ej. Auth0) con un mensaje para el usuario. */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> estado(IllegalStateException ex) {
        log.error("Operación no disponible: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "status", 503,
                "message", ex.getMessage() != null ? ex.getMessage() : "Servicio no disponible",
                "timestamp", OffsetDateTime.now().toString()));
    }
}
