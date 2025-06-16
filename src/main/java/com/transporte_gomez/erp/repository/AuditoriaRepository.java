package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.AuditoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<AuditoriaEntity, Long> {
    @Query("select a from AuditoriaEntity a where a.moduloId = ?1 and a.entidadId = ?2")
    List<AuditoriaEntity> findByModuloIdAndEntidadId(Integer moduloId, Long entidadId);
}