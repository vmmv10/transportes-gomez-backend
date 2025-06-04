package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.DocumentoEntity;
import com.transporte_gomez.erp.entity.ItemEntity;
import graphql.org.antlr.v4.runtime.misc.MultiMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoRepository extends JpaRepository<DocumentoEntity, Long> {
    Page<DocumentoEntity> findAll(Specification<DocumentoEntity> documentoEntitySpecification, Pageable pageable);
}