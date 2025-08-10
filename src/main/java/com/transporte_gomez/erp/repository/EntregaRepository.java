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
    SELECT TO_CHAR(e.creado_en, 'YYYY-MM') AS mes, COUNT(*) AS total
    FROM qa.entregas e
    INNER JOIN qa.ordenes_servicios os ON os.id = e.orden_servicio_id
    WHERE e.entregado = true
      AND (:escuela IS NULL OR os.escuela_id = :escuela)
    GROUP BY TO_CHAR(e.creado_en, 'YYYY-MM')
    ORDER BY mes
""", nativeQuery = true)
    List<Object[]> contarEntregasEntregadasPorMes(@Param("escuela") Long escuela);

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
    SELECT\s
        TO_CHAR(e.creado_en, 'YYYY-MM-DD') AS dia,\s
        COUNT(*) AS total
    FROM qa.entregas e
    INNER JOIN qa.ordenes_servicios os ON os.id = e.orden_servicio_id
    WHERE e.entregado = true
      AND (:escuela IS NULL OR os.escuela_id = :escuela)
      AND e.creado_en >= CURRENT_DATE - INTERVAL '4 days'  -- últimos 5 días incluyendo hoy
    GROUP BY TO_CHAR(e.creado_en, 'YYYY-MM-DD')
    ORDER BY dia
    
""", nativeQuery = true)
    List<Object[]> contarEntregasEntregadasUltimosCincoDias(@Param("escuela") Long escuela);

    @Query(value = """
        SELECT 
            es.nombre AS nombreEscuela, 
            COUNT(*) AS totalEntregas
        FROM qa.entregas e
        INNER JOIN qa.ordenes_servicios os ON os.id = e.orden_servicio_id
        INNER JOIN qa.escuelas es ON es.id = os.escuela_id
        WHERE e.entregado = true
        GROUP BY es.nombre
        ORDER BY totalEntregas DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<Object[]> findTopEscuelasConMasEntregas(@Param("limit") int limit);

}
