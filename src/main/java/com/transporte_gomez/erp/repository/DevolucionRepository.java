package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.DevolucionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DevolucionRepository extends JpaRepository<DevolucionEntity, Long> {
    Page<DevolucionEntity> findAll(Specification<DevolucionEntity> devolucionEntitySpecification, Pageable pageable);

    @Query("select d from DevolucionEntity d where d.user = ?1 and d.estado = ?2")
    List<DevolucionEntity> findByUserAndEstado(Long user, Integer estado);
}