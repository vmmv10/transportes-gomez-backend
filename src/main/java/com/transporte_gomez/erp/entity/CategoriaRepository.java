package com.transporte_gomez.erp.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Integer> {
  @Query("select c from CategoriaEntity c where c.nombre = ?1")
  CategoriaEntity findByNombre(String nombre);
}