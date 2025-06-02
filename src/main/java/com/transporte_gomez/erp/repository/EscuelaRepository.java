package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.EscuelaEntity;
import org.hibernate.query.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;

public interface EscuelaRepository extends JpaRepository<EscuelaEntity, Long> {
    @Query("SELECT DISTINCT e.comuna FROM EscuelaEntity e WHERE e.comuna IS NOT NULL ORDER BY e.comuna")
    List<String> findDistinctComunas();

}
