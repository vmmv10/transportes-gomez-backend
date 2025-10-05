package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrdenServicioRepository extends JpaRepository<OrdenServicioEntity, Long> {
    Page<OrdenServicioEntity> findAll(Specification<OrdenServicioEntity> ordenServicioSpecification, Pageable pageable);

    @Query(value = """
        SELECT
            e.id              AS escuela_id,
            e.nombre          AS escuela,
            i.id              AS item_id,
            i.nombre          AS item_nombre,
            SUM(d.cantidad)   AS cantidad
        FROM qa.ordenes_servicios os
        JOIN qa.escuelas e
            ON e.id = os.escuela_id
        JOIN qa.ordenes_servicios_detalles d
            ON d.orden_servicio = os.id
        JOIN qa.item i
            ON i.id = d.item
        WHERE (:entregado IS NULL OR os.entregado = :entregado)
          AND (:escuela_id IS NULL OR os.escuela_id = :escuela_id)
          AND (:item_id IS NULL OR i.id = :item_id)
          AND (:item_nombre IS NULL OR i.nombre ILIKE '%' || :item_nombre || '%')
        GROUP BY e.id, e.nombre, i.id, i.nombre
        ORDER BY cantidad DESC
        """,
            countQuery = """
        SELECT COUNT(*) 
        FROM (
            SELECT 1
            FROM qa.ordenes_servicios os
            JOIN qa.escuelas e
                ON e.id = os.escuela_id
            JOIN qa.ordenes_servicios_detalles d
                ON d.orden_servicio = os.id
            JOIN qa.item i
                ON i.id = d.item
            WHERE (:entregado IS NULL OR os.entregado = :entregado)
              AND (:escuela_id IS NULL OR os.escuela_id = :escuela_id)
              AND (:item_id IS NULL OR i.id = :item_id)
              AND (:item_nombre IS NULL OR i.nombre ILIKE '%' || :item_nombre || '%')
            GROUP BY e.id, e.nombre, i.id, i.nombre
        ) AS sub
        """,
            nativeQuery = true)
    Page<Object[]> buscarOrdenesServicioConDetalles(
            @Param("escuela_id") Long escuela_id,
            @Param("item_id") Long item_id,
            @Param("item_nombre") String item_nombre,
            @Param("entregado") Boolean entregado,
            Pageable pageable);

}