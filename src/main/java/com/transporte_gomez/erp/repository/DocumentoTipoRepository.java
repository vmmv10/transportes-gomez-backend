package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.DocumentoTipoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DocumentoTipoRepository extends JpaRepository<DocumentoTipoEntity, Integer> {
    @Query("select d from DocumentoTipoEntity d where d.codigo = ?1")
    DocumentoTipoEntity findByCodigo(Integer codigo);
}