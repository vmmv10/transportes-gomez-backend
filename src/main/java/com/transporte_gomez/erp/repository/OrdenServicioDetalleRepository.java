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
        AND (:oc IS NULL OR os.documentoReferencia = :oc)
        AND (CAST(:categoria AS INTEGER) IS NULL OR os.categoria.id = :categoria)
    GROUP BY i.nombre
    ORDER BY SUM(d.cantidad) DESC
""")
    List<Object[]> findItemsMasDespachadosPorEscuela(@Param("escuelaId") Long escuelaId, @Param("oc") String oc,
                                                     @Param("categoria") Integer categoria);

    @Query("select o from OrdenServicioDetalleEntity o where o.ordenServicio.id = ?1")
    List<OrdenServicioDetalleEntity> findByOrdenServicio_Id(Long id);

}