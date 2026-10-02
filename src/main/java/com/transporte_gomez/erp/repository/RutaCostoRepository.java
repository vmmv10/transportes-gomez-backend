package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.RutaCostoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RutaCostoRepository extends JpaRepository<RutaCostoEntity, Long> {

    List<RutaCostoEntity> findByRuta_IdOrderByFechaAscIdAsc(Integer rutaId);

    long countByVehiculo_Id(Long vehiculoId);

    @Query("select coalesce(sum(c.monto), 0) from RutaCostoEntity c where c.ruta.id = ?1")
    java.math.BigDecimal totalPorRuta(Integer rutaId);

    /**
     * Resumen por ruta en un rango de fechas. Una fila por ruta y tipo de costo
     * (tipo nulo si la ruta no tiene costos).
     * Columnas: ruta, fecha, estado, chofer, vehiculo_id, vehiculo, kilometros, entregas, tipo, monto, litros
     */
    @Query(value = """
            SELECT r.id                                          AS ruta,
                   r.fecha                                       AS fecha,
                   r.estado                                      AS estado,
                   trim(concat_ws(' ', u.nombre, u.apellidos))   AS chofer,
                   v.id                                          AS vehiculo_id,
                   CASE WHEN v.id IS NULL THEN NULL
                        ELSE v.nombre || coalesce(' · ' || nullif(trim(v.patente), ''), '') END AS vehiculo,
                   coalesce(r.kilometros,
                            CASE WHEN r.km_llegada IS NOT NULL AND r.km_salida IS NOT NULL
                                 THEN r.km_llegada - r.km_salida END)   AS kilometros,
                   (SELECT count(*) FROM entregas e WHERE e.ruta_id = r.id) AS entregas,
                   c.tipo                                        AS tipo,
                   coalesce(sum(c.monto), 0)                     AS monto,
                   sum(c.litros)                                 AS litros
            FROM rutas r
            JOIN usuarios u ON u.id = r.chofer_id
            LEFT JOIN vehiculos v ON v.id = r.vehiculo_id
            LEFT JOIN rutas_costos c ON c.ruta_id = r.id
            WHERE r.fecha BETWEEN :desde AND :hasta
              AND (CAST(:vehiculo AS bigint) IS NULL OR r.vehiculo_id = :vehiculo
                   OR EXISTS (SELECT 1 FROM rutas_costos x WHERE x.ruta_id = r.id AND x.vehiculo_id = :vehiculo))
              AND (CAST(:chofer AS bigint) IS NULL OR r.chofer_id = :chofer)
              AND (:conCostos = false OR EXISTS (SELECT 1 FROM rutas_costos x WHERE x.ruta_id = r.id))
            GROUP BY r.id, r.fecha, r.estado, u.nombre, u.apellidos, v.id, v.nombre, v.patente, c.tipo
            ORDER BY r.fecha DESC, r.id DESC
            """, nativeQuery = true)
    List<Object[]> resumen(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta, @Param("vehiculo") Long vehiculo,
                           @Param("chofer") Long chofer, @Param("conCostos") boolean conCostos);
}
