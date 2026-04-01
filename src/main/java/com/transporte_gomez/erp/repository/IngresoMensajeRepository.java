package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.IngresoMensajeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IngresoMensajeRepository extends JpaRepository<IngresoMensajeEntity, Long> {
    @Query("select i from IngresoMensajeEntity i where i.conversacion.id = ?1 order by i.fecha")
    List<IngresoMensajeEntity> findByConversacion_IdOrderByFechaAsc(Long id);
}