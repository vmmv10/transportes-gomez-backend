package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.EntregaEntity;
import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import com.transporte_gomez.erp.entity.RutaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EntregaRepository extends JpaRepository<EntregaEntity, Integer> {
    @Query("select e from EntregaEntity e where e.ruta.id = ?1")
    List<EntregaEntity> findByRuta_Id(Integer id);

    Page<EntregaEntity> findAll(Specification<EntregaEntity> spec, Pageable pageable);

    @Transactional
    @Modifying
    @Query("delete from EntregaEntity e where e.ruta.id = ?1 and e.ordenServicio.id = ?2")
    void deleteByRutaAndOrdenServicio(Long ruta, Long ordenServicio);

    @Query("select e from EntregaEntity e where e.ruta.id = ?1 and e.ordenServicio.id = ?2")
    Optional<EntregaEntity> findByRuta_IdAndOrdenServicio_Id(Integer id, Long id1);

    @Query(value = """
    WITH rango AS (
        SELECT
            DATE_TRUNC('month', MIN(creado_en)) AS inicio,
            DATE_TRUNC('month', MAX(creado_en)) AS fin
        FROM qa.entregas
    ),
    meses AS (
        SELECT generate_series(
            (SELECT inicio FROM rango),
            (SELECT fin FROM rango),
            interval '1 month'
        ) AS mes_inicio
    )
    SELECT
        CASE EXTRACT(MONTH FROM m.mes_inicio)
            WHEN 1 THEN 'Enero'
            WHEN 2 THEN 'Febrero'
            WHEN 3 THEN 'Marzo'
            WHEN 4 THEN 'Abril'
            WHEN 5 THEN 'Mayo'
            WHEN 6 THEN 'Junio'
            WHEN 7 THEN 'Julio'
            WHEN 8 THEN 'Agosto'
            WHEN 9 THEN 'Septiembre'
            WHEN 10 THEN 'Octubre'
            WHEN 11 THEN 'Noviembre'
            WHEN 12 THEN 'Diciembre'
        END
        || ' ' || EXTRACT(YEAR FROM m.mes_inicio) AS mes,
        COUNT(e.id) AS total
    FROM meses m
    LEFT JOIN qa.entregas e
        ON DATE_TRUNC('month', e.creado_en) = m.mes_inicio
    LEFT JOIN qa.ordenes_servicios os
        ON os.id = e.orden_servicio_id
    WHERE e.entregado = true
      AND (:escuela IS NULL OR os.escuela_id = :escuela)
    GROUP BY m.mes_inicio
    HAVING COUNT(e.id) > 0
    ORDER BY m.mes_inicio;
""", nativeQuery = true)
    List<Object[]> contarEntregasPorMesIncluyendoCeros(@Param("escuela") Long escuela);

    @Query(value = """
    SELECT TO_CHAR(e.creado_en, 'YYYY-MM-DD') AS dia, COUNT(*) AS total
    FROM qa.entregas e
    INNER JOIN qa.ordenes_servicios os ON os.id = e.orden_servicio_id
    WHERE e.entregado = true
      AND (:escuela IS NULL OR os.escuela_id = :escuela)
    GROUP BY TO_CHAR(e.creado_en, 'YYYY-MM-DD')
    ORDER BY dia
""", nativeQuery = true)
    List<Object[]> contarEntregasEntregadasPorDia(@Param("escuela") Long escuela);

    @Query(value = """
    SELECT TO_CHAR(DATE_TRUNC('week', e.creado_en), 'IYYY-IW') AS semana, COUNT(*) AS total
    FROM qa.entregas e
    INNER JOIN qa.ordenes_servicios os ON os.id = e.orden_servicio_id
    WHERE e.entregado = true
      AND (:escuela IS NULL OR os.escuela_id = :escuela)
    GROUP BY DATE_TRUNC('week', e.creado_en)
    ORDER BY semana
""", nativeQuery = true)
    List<Object[]> contarEntregasEntregadasPorSemana(@Param("escuela") Long escuela);

    @Query(value = """
    SELECT 
        TO_CHAR(e.creado_en, 'YYYY-MM-DD') AS dia,
        COUNT(*) AS total
    FROM qa.entregas e
    INNER JOIN qa.ordenes_servicios os ON os.id = e.orden_servicio_id
    WHERE e.entregado = true
      AND (:escuela IS NULL OR os.escuela_id = :escuela)
      AND e.creado_en >= CURRENT_DATE - INTERVAL '4 days'
    GROUP BY TO_CHAR(e.creado_en, 'YYYY-MM-DD')
    ORDER BY dia
""", nativeQuery = true)
    List<Object[]> contarEntregasEntregadasUltimosCincoDias(@Param("escuela") Long escuela);

    @Query(value = """
    SELECT 
        es.nombre AS nombreEscuela, 
        COUNT(DISTINCT DATE(e.fecha)) AS totalEntregas
    FROM qa.entregas e
    INNER JOIN qa.ordenes_servicios os ON os.id = e.orden_servicio_id
    INNER JOIN qa.escuelas es ON es.id = os.escuela_id
    WHERE e.entregado = true
      AND (:escuela IS NULL OR os.escuela_id = :escuela)
    GROUP BY es.nombre
    ORDER BY totalEntregas DESC
    LIMIT :limit
""", nativeQuery = true)
    List<Object[]> findTopEscuelasConMasEntregas(
            @Param("escuela") Long escuela,
            @Param("limit") int limit
    );

    @Query(value = """
    SELECT 
        CASE WHEN e.entregado = true THEN 'Entregadas' ELSE 'No entregadas' END AS estado,
        COUNT(*) AS total
    FROM qa.entregas e
    INNER JOIN qa.ordenes_servicios os ON os.id = e.orden_servicio_id
    WHERE (:escuela IS NULL OR os.escuela_id = :escuela)
    GROUP BY estado
""", nativeQuery = true)
    List<Object[]> contarEntregasEntregadasVsNoEntregadas(@Param("escuela") Long escuela);

    @Query(value = """
    SELECT 
        e.id,
        TO_CHAR(e.creado_en, 'YYYY-MM-DD HH24:MI') AS fecha,
        es.nombre AS escuela,
        CASE WHEN e.entregado = true THEN 'Sí' ELSE 'No' END AS entregado
    FROM qa.entregas e
    INNER JOIN qa.ordenes_servicios os ON os.id = e.orden_servicio_id
    INNER JOIN qa.escuelas es ON es.id = os.escuela_id
    WHERE (:escuela IS NULL OR os.escuela_id = :escuela)
    ORDER BY e.creado_en DESC
    LIMIT :limit
""", nativeQuery = true)
    List<Object[]> ultimasEntregas(@Param("escuela") Long escuela, @Param("limit") int limit);

    @Query(value = """
    SELECT 
        es.nombre AS escuela,
        COUNT(*) AS total_pendientes
    FROM qa.entregas e
    INNER JOIN qa.ordenes_servicios os ON os.id = e.orden_servicio_id
    INNER JOIN qa.escuelas es ON es.id = os.escuela_id
    WHERE e.entregado = false
      AND (:escuela IS NULL OR os.escuela_id = :escuela)
    GROUP BY es.nombre
    ORDER BY total_pendientes DESC
""", nativeQuery = true)
    List<Object[]> escuelasConPendientes(@Param("escuela") Long escuela);

    @Query(value = """
    SELECT 
        ROUND(COUNT(*)::numeric / COUNT(DISTINCT DATE(e.creado_en)), 2) AS promedio_diario
    FROM qa.entregas e
    INNER JOIN qa.ordenes_servicios os ON os.id = e.orden_servicio_id
    WHERE (:escuela IS NULL OR os.escuela_id = :escuela)
""", nativeQuery = true)
    Double promedioEntregasDiarias(@Param("escuela") Long escuela);

    @Query(value = """
    SELECT 
        ROUND(
            (SUM(CASE WHEN e.entregado = true THEN 1 ELSE 0 END)::numeric / COUNT(*)) * 100, 2
        ) AS porcentaje_cumplimiento
    FROM qa.entregas e
    INNER JOIN qa.ordenes_servicios os ON os.id = e.orden_servicio_id
    WHERE (:escuela IS NULL OR os.escuela_id = :escuela)
""", nativeQuery = true)
    Double porcentajeCumplimiento(@Param("escuela") Long escuela);

    @Query("""
    SELECT COUNT(e)
    FROM EntregaEntity e
    WHERE e.ruta.fecha BETWEEN :fechaInicio AND :fechaFin
      AND e.ordenServicio.escuela.id = :escuelaId
""")
    Long countEntregasPorEscuelaEntreFechas(@Param("escuelaId") Long escuelaId,
                                            @Param("fechaInicio") LocalDate fechaInicio,
                                            @Param("fechaFin") LocalDate fechaFin);

    @Query("select e from EntregaEntity e where e.ordenServicio.id = ?1")
    Optional<EntregaEntity> findByOrdenServicio_Id(Long id);

    @Query(value = """
   SELECT
          COUNT(*) AS total,
          COUNT(*) FILTER (WHERE e.entregado = true) AS realizadas,
          COUNT(*) FILTER (WHERE e.entregado = false) AS pendientes
      FROM qa.entregas e
      JOIN qa.ordenes_servicios os
          ON os.id = e.orden_servicio_id
      JOIN qa.rutas r
          ON r.id = e.ruta_id
    WHERE (:escuelaId IS NULL OR os.escuela_id = :escuelaId)
      AND (:oc IS NULL OR os.documento_referencia = :oc)
        AND (CAST(:categoria AS INTEGER) IS NULL OR os.categoria = :categoria)
        AND (DATE(r.fecha) = COALESCE(CAST(:fecha AS date), DATE(r.fecha)))
        
""", nativeQuery = true)
    List<Object[]> getEntregaStats(@Param("escuelaId") Long escuela,
                                   @Param("fecha") LocalDate fecha, @Param("oc") String oc, @Param("categoria") Integer categoria);

    @Query(value = """
        SELECT COUNT(*)
        FROM qa.entregas e
        JOIN qa.ordenes_servicios os ON os.id = e.orden_servicio_id
        JOIN qa.rutas r ON r.id = e.ruta_id
        WHERE e.entregado = false
          AND (:escuela IS NULL OR os.escuela_id = :escuela)
          AND DATE(r.fecha AT TIME ZONE 'America/Santiago') = CURRENT_DATE
        AND (:oc IS NULL OR os.documento_referencia = :oc)
        AND (CAST(:categoria AS INTEGER) IS NULL OR os.categoria = :categoria)
    """, nativeQuery = true)
    Long getEntregasHoy(
            @Param("escuela") Long escuela,
            @Param("oc") String oc,
            @Param("categoria") Integer categoria
    );

    @Query(value = """
    WITH kpi_diario AS (
        SELECT
            r.fecha AS dia,

            COUNT(e.id) FILTER (
                WHERE e.entregado = true
                AND e.fecha::date = r.fecha
            ) AS entregas_realizadas,

            COUNT(e.id) FILTER (
                WHERE e.fecha::date = r.fecha
            ) AS entregas_planificadas,

            CASE
                WHEN COUNT(e.id) FILTER (WHERE e.fecha::date = r.fecha) = 0
                    THEN NULL
                ELSE ROUND(
                    (
                        COUNT(e.id) FILTER (
                            WHERE e.entregado = true
                            AND e.fecha::date = r.fecha
                        )::numeric
                        /
                        COUNT(e.id) FILTER (
                            WHERE e.fecha::date = r.fecha
                        )::numeric
                    ) * 100, 2
                )
            END AS kpi
        FROM qa.rutas r
        LEFT JOIN qa.entregas e
            ON e.ruta_id = r.id
        LEFT JOIN qa.ordenes_servicios os
            ON os.id = e.orden_servicio_id
        WHERE
            (:fecha IS NULL OR r.fecha = :fecha)
            AND (:escuelaId IS NULL OR os.escuela_id = :escuelaId)
            AND (:categoriaId IS NULL OR os.categoria = :categoriaId)
        GROUP BY r.fecha
    )

    SELECT AVG(kpi)
    FROM kpi_diario
    WHERE entregas_planificadas > 0
    """,
            nativeQuery = true)
    BigDecimal getPromedioKPIEntregasATiempo(
            @Param("escuelaId") Long escuelaId,
            @Param("fecha") LocalDate fecha,
            @Param("categoriaId") Integer categoriaId
    );

    @Query(value = """
    WITH kpi_quiebre AS (
        SELECT
            r.fecha AS dia,

            COUNT(os.id) AS pedidos_totales,

            COUNT(os.id) FILTER (WHERE os.entregado = false) AS pedidos_no_completados,

            CASE
                WHEN COUNT(os.id) = 0 THEN NULL
                ELSE ROUND(
                    (COUNT(os.id) FILTER (WHERE os.entregado = false)::numeric /
                    COUNT(os.id)::numeric) * 100,
                2)
            END AS kpi
        FROM qa.rutas r
        LEFT JOIN qa.entregas e
            ON e.ruta_id = r.id
        LEFT JOIN qa.ordenes_servicios os
            ON os.id = e.orden_servicio_id
        WHERE
            (:fecha IS NULL OR r.fecha = :fecha)
            AND (:escuelaId IS NULL OR os.escuela_id = :escuelaId)
            AND (:categoriaId IS NULL OR os.categoria = :categoriaId)
        GROUP BY r.fecha
    )
    
    SELECT AVG(kpi)
    FROM kpi_quiebre
    WHERE pedidos_totales > 0
    """,
            nativeQuery = true)
    BigDecimal getPromedioKPIQuiebreStock(
            @Param("escuelaId") Long escuelaId,
            @Param("fecha") LocalDate fecha,
            @Param("categoriaId") Integer categoriaId
    );

    @Query(value = """
    SELECT 
        AVG(EXTRACT(EPOCH FROM (e.fecha - i.fecha_cierre)) / 3600) 
            AS promedio_horas_respuesta
    FROM qa.entregas e
    JOIN qa.rutas r 
        ON r.id = e.ruta_id
    JOIN qa.ordenes_servicios os 
        ON os.id = e.orden_servicio_id
    JOIN qa.ingresos i 
        ON i.id = os.ingreso
    WHERE
        os.ingreso IS NOT NULL
        AND i.fecha_cierre IS NOT NULL
        AND e.fecha IS NOT NULL
        AND (:fecha IS NULL OR r.fecha = :fecha)
        AND (:escuelaId IS NULL OR os.escuela_id = :escuelaId)
        AND (:categoriaId IS NULL OR os.categoria = :categoriaId)
    """,
            nativeQuery = true)
    BigDecimal getPromedioTiempoRespuestaInterno(
            @Param("escuelaId") Long escuelaId,
            @Param("fecha") LocalDate fecha,
            @Param("categoriaId") Integer categoriaId
    );

}
