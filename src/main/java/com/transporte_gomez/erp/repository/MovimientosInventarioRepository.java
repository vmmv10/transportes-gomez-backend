package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.MovimientosInventarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientosInventarioRepository extends JpaRepository<MovimientosInventarioEntity, Integer> {
}