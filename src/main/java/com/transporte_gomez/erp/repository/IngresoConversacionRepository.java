package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.IngresoConversacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IngresoConversacionRepository extends JpaRepository<IngresoConversacionEntity, Long> {
  @Query("select i from IngresoConversacionEntity i where i.ingreso.id = ?1")
  IngresoConversacionEntity findByIngreso_Id(Integer id);
}