package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.DocumentoEntity;
import com.transporte_gomez.erp.entity.ItemEntity;
import graphql.org.antlr.v4.runtime.misc.MultiMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DocumentoRepository extends JpaRepository<DocumentoEntity, Long> {
    Page<DocumentoEntity> findAll(Specification<DocumentoEntity> documentoEntitySpecification, Pageable pageable);

    @Query("select d from DocumentoEntity d where d.numero = ?1 and d.tipo.codigo = ?2")
    DocumentoEntity findByNumeroAndTipo_Codigo(String numero, Integer codigo);

    @Query("select d from DocumentoEntity d where d.numero = ?1")
    DocumentoEntity findByNumero(Long numero);
}