package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.DevolucionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DevolucionRepository extends JpaRepository<DevolucionEntity, Long> {
    Page<DevolucionEntity> findAll(Specification<DevolucionEntity> devolucionEntitySpecification, Pageable pageable);
}