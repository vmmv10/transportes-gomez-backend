package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.MovimientosInventarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MovimientosInventarioRepository extends JpaRepository<MovimientosInventarioEntity, Integer> {
    @Query("""
            select m from MovimientosInventarioEntity m
            where m.bodega.id = ?1 and m.tipo = ?2 and m.item.id = ?3 and m.entidadId = ?4""")
    MovimientosInventarioEntity findByBodega_IdAndTipoAndItem_IdAndEntidadId(Long id, Integer tipo, Long id1, Long entidadId);
}