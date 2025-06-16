package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenServicioRepository extends JpaRepository<OrdenServicioEntity, Long> {
    Page<OrdenServicioEntity> findAll(Specification<OrdenServicioEntity> ordenServicioSpecification, Pageable pageable);
}