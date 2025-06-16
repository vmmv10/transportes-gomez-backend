package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.RutaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutaRepository extends JpaRepository<RutaEntity, Integer> {
    Page<RutaEntity> findAll(Specification<RutaEntity> rutaEntitySpecification, Pageable pageable);
}