package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.IngresosEmergenciaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IngresosEmergenciaRepository extends JpaRepository<IngresosEmergenciaEntity, Integer> {
    Page<IngresosEmergenciaEntity> findAll(Specification<IngresosEmergenciaEntity> spec, Pageable pageable);

    @Query("select i from IngresosEmergenciaEntity i where i.user = ?1 and i.estado = ?2 order by i.id asc ")
    List<IngresosEmergenciaEntity> findByUserAndEstado(Long user, Integer estado);
}