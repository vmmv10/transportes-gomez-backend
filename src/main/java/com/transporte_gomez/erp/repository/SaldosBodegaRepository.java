package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.SaldosBodegaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SaldosBodegaRepository extends JpaRepository<SaldosBodegaEntity, Integer> {
    @Query("select s from SaldosBodegaEntity s where s.bodega.id = ?1 and s.item.id = ?2")
    SaldosBodegaEntity findByBodega_IdAndItem_Id(Long id, Long id1);

    Page<SaldosBodegaEntity> findAll(Specification<SaldosBodegaEntity> spec, Pageable pageable);

    @Query("select s from SaldosBodegaEntity s where s.item.codigo = ?1")
    SaldosBodegaEntity findByItem_Codigo(String codigo);
}