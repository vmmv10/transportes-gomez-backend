package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.MarcaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MarcaRepository extends JpaRepository<MarcaEntity, Integer> {
  @Query("select m from MarcaEntity m where m.nombre = ?1")
  MarcaEntity findByNombre(String nombre);

  Page<MarcaEntity> findAll(Specification<MarcaEntity> marcaEntitySpecification, Pageable pageable);
}