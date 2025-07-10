package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.SaldosBodegaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SaldosBodegaRepository extends JpaRepository<SaldosBodegaEntity, Integer> {
    @Query("select s from SaldosBodegaEntity s where s.bodega.id = ?1 and s.item.id = ?2")
    SaldosBodegaEntity findByBodega_IdAndItem_Id(Long id, Long id1);
}