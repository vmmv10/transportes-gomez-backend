package com.transporte_gomez.erp.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Cifras públicas de cobertura para la landing: solo totales agregados por comuna,
 * sin nombres de clientes ni de destinos, ni datos de las órdenes.
 */
public record CoberturaPublica(
        long entregas,
        long destinos,
        long comunas,
        LocalDate desde,
        List<Comuna> porComuna) {

    public record Comuna(String nombre, long entregas, long destinos) {
    }

    /** Vehículos activos de un tipo (flota propia y en convenio), sin patentes ni nombres. */
    public record FlotaTipo(String tipo, long propios, long arrendados) {
    }

    /** Un destino con entregas realizadas, para el mapa (sin nombre: solo comuna, ubicación y total). */
    public record Punto(long id, String comuna, double lat, double lng, long total) {
    }
}
