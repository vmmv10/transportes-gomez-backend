package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.BultoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BultoRepository extends JpaRepository<BultoEntity, Long> {

    List<BultoEntity> findByIngreso_IdOrderByIdAsc(Integer ingresoId);

    List<BultoEntity> findByCodigoExternoIgnoreCase(String codigoExterno);
}
