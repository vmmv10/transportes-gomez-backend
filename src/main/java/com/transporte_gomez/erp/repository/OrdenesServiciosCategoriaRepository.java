package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.OrdenesServiciosCategoriaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdenesServiciosCategoriaRepository extends JpaRepository<OrdenesServiciosCategoriaEntity, Integer> {
    Page<OrdenesServiciosCategoriaEntity> findAll(Specification<OrdenesServiciosCategoriaEntity> spec, Pageable pageable);

    @Query("select o from OrdenesServiciosCategoriaEntity o where o.activo = ?1")
    List<OrdenesServiciosCategoriaEntity> findByActivo(Boolean activo);
}