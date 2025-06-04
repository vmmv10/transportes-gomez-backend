package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.ProveedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long> {
}