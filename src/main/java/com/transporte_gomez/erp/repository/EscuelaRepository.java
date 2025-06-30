package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.DocumentoEntity;
import com.transporte_gomez.erp.entity.EscuelaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EscuelaRepository extends JpaRepository<EscuelaEntity, Long> {
    @Query("SELECT DISTINCT e.comuna FROM EscuelaEntity e WHERE e.comuna IS NOT NULL ORDER BY e.comuna")
    List<String> findDistinctComunas();

    @Query("select e from EscuelaEntity e where e.rbd = ?1")
    EscuelaEntity findByRbd(String rbd);

    Page<EscuelaEntity> findAll(Specification<EscuelaEntity> escuelaEntitySpecificatio, Pageable pageable);
}
