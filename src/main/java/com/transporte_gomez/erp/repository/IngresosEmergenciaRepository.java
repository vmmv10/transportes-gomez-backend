package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.IngresosEmergenciaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngresosEmergenciaRepository extends JpaRepository<IngresosEmergenciaEntity, Integer> {
    Page<IngresosEmergenciaEntity> findAll(Specification<IngresosEmergenciaEntity> spec, Pageable pageable);
}