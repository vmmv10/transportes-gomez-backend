package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.SeguimientoEventoEntity;
import com.transporte_gomez.erp.enums.SeguimientoTipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeguimientoEventoRepository extends JpaRepository<SeguimientoEventoEntity, Long> {

    List<SeguimientoEventoEntity> findByOrdenServicio_IdOrderByFechaAscIdAsc(Long ordenServicioId);

    boolean existsByEntregaIdAndRutaIdAndTipo(Integer entregaId, Integer rutaId, SeguimientoTipo tipo);
}
