package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.OrdenServicioDetalleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrdenServicioDetalleRepository extends JpaRepository<OrdenServicioDetalleEntity, Long> {
    @Query("""
    SELECT i.nombre, SUM(d.cantidad)
    FROM OrdenServicioDetalleEntity d,
         ItemEntity i
    JOIN d.ordenServicio os
    WHERE os.entregado = true
      AND d.item IS NOT NULL
      AND i.id = d.item
      AND (:escuelaId IS NULL OR os.escuela.id = :escuelaId)
    GROUP BY i.nombre
    ORDER BY SUM(d.cantidad) DESC
""")
    List<Object[]> findItemsMasDespachadosPorEscuela(@Param("escuelaId") Long escuelaId);


}