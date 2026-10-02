package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.dto.CoberturaPublica;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Totales de entregas realizadas por comuna y destinos atendidos, para la página de cobertura
 * de la landing (todos los clientes; sin datos que identifiquen a clientes ni destinos).
 * Se calculan a lo más una vez por hora (la consulta recorre todas las órdenes).
 */
@Service
public class CoberturaPublicaService {

    private static final Duration VIGENCIA = Duration.ofHours(1);

    @PersistenceContext
    private EntityManager em;

    private volatile CoberturaPublica cache;
    private volatile Instant calculado = Instant.EPOCH;
    private volatile List<CoberturaPublica.Punto> puntos;
    private volatile List<CoberturaPublica.FlotaTipo> flota;
    private volatile Instant flotaCalculada = Instant.EPOCH;

    /** Vehículos activos que no están fuera de servicio, agrupados por tipo. */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<CoberturaPublica.FlotaTipo> flota() {
        if (flota == null || Instant.now().isAfter(flotaCalculada.plus(VIGENCIA))) {
            List<Object[]> filas = em.createNativeQuery("""
                    SELECT tipo,
                           count(*) FILTER (WHERE propiedad = 'PROPIO'),
                           count(*) FILTER (WHERE propiedad = 'ARRENDADO')
                    FROM vehiculos
                    WHERE activo AND estado <> 'FUERA_DE_SERVICIO'
                    GROUP BY tipo
                    ORDER BY count(*) DESC, tipo
                    """).getResultList();
            List<CoberturaPublica.FlotaTipo> lista = new ArrayList<>();
            for (Object[] f : filas) {
                lista.add(new CoberturaPublica.FlotaTipo((String) f[0], n(f[1]), n(f[2])));
            }
            flota = List.copyOf(lista);
            flotaCalculada = Instant.now();
        }
        return flota;
    }
    private volatile Instant puntosCalculados = Instant.EPOCH;

    /**
     * Destinos con entregas realizadas y coordenadas válidas. No se publica el nombre del destino
     * ni el cliente: solo la comuna, la ubicación y la cantidad de entregas.
     */
    @Transactional(readOnly = true)
    public List<CoberturaPublica.Punto> puntos() {
        if (puntos == null || Instant.now().isAfter(puntosCalculados.plus(VIGENCIA))) {
            synchronized (this) {
                if (puntos == null || Instant.now().isAfter(puntosCalculados.plus(VIGENCIA))) {
                    puntos = calcularPuntos();
                    puntosCalculados = Instant.now();
                }
            }
        }
        return puntos;
    }

    @SuppressWarnings("unchecked")
    private List<CoberturaPublica.Punto> calcularPuntos() {
        List<Object[]> filas = em.createNativeQuery("""
                SELECT d.id, c.nombre, d.latitud, d.longitud, count(*)
                FROM ordenes_servicios o
                         JOIN destinos d ON d.id = o.destino_id
                         LEFT JOIN comunas c ON c.id = d.comuna_id
                WHERE o.entregado
                  AND coalesce(trim(d.latitud), '') <> ''
                  AND coalesce(trim(d.longitud), '') <> ''
                GROUP BY d.id, c.nombre, d.latitud, d.longitud
                ORDER BY count(*) DESC
                """).getResultList();
        List<CoberturaPublica.Punto> lista = new ArrayList<>();
        for (Object[] f : filas) {
            Double lat = coordenada(f[2]);
            Double lng = coordenada(f[3]);
            // Solo puntos dentro de Chiloé y alrededores (descarta coordenadas mal ingresadas)
            if (lat == null || lng == null || lat < -44.5 || lat > -41.3 || lng < -75.5 || lng > -72.3) {
                continue;
            }
            lista.add(new CoberturaPublica.Punto(n(f[0]), (String) f[1], lat, lng, n(f[4])));
        }
        return List.copyOf(lista);
    }

    /** Las coordenadas se guardan como texto y a veces con coma decimal. */
    private static Double coordenada(Object v) {
        if (v == null) {
            return null;
        }
        try {
            return Double.parseDouble(v.toString().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public CoberturaPublica obtener() {
        if (cache == null || Instant.now().isAfter(calculado.plus(VIGENCIA))) {
            synchronized (this) {
                if (cache == null || Instant.now().isAfter(calculado.plus(VIGENCIA))) {
                    cache = calcular();
                    calculado = Instant.now();
                }
            }
        }
        return cache;
    }

    @SuppressWarnings("unchecked")
    private CoberturaPublica calcular() {
        // Entregas realizadas por comuna del destino
        List<Object[]> filas = em.createNativeQuery("""
                SELECT c.nombre,
                       count(*)             AS entregas,
                       count(DISTINCT d.id) AS destinos
                FROM ordenes_servicios o
                         JOIN destinos d ON d.id = o.destino_id
                         JOIN comunas c ON c.id = d.comuna_id
                WHERE o.entregado
                GROUP BY c.nombre
                ORDER BY count(*) DESC, c.nombre
                """).getResultList();

        List<CoberturaPublica.Comuna> porComuna = new ArrayList<>();
        for (Object[] f : filas) {
            porComuna.add(new CoberturaPublica.Comuna((String) f[0], n(f[1]), n(f[2])));
        }

        Object[] t = (Object[]) em.createNativeQuery("""
                SELECT count(*),
                       count(DISTINCT d.id),
                       count(DISTINCT d.comuna_id),
                       min(o.fecha)::date
                FROM ordenes_servicios o
                         JOIN destinos d ON d.id = o.destino_id
                WHERE o.entregado
                """).getSingleResult();

        LocalDate desde = t[3] instanceof Date fecha ? fecha.toLocalDate() : t[3] instanceof LocalDate ld ? ld : null;
        return new CoberturaPublica(n(t[0]), n(t[1]), n(t[2]), desde, List.copyOf(porComuna));
    }

    private static long n(Object v) {
        return v == null ? 0 : ((Number) v).longValue();
    }
}
