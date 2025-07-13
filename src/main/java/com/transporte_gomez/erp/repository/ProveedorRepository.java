package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.ProveedorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long> {
    Page<ProveedorEntity> findAll(Specification<ProveedorEntity> proveedorEntitySpecification, Pageable pageable);

    @Query("select p from ProveedorEntity p where p.activo = ?1")
    List<ProveedorEntity> findByActivo(Boolean activo);
}