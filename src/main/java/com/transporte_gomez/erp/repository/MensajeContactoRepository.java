package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.MensajeContactoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeContactoRepository extends JpaRepository<MensajeContactoEntity, Long> {

    Page<MensajeContactoEntity> findByAtendido(Boolean atendido, Pageable pageable);

    Page<MensajeContactoEntity> findByMotivo(com.transporte_gomez.erp.enums.MensajeMotivo motivo, Pageable pageable);

    Page<MensajeContactoEntity> findByAtendidoAndMotivo(Boolean atendido, com.transporte_gomez.erp.enums.MensajeMotivo motivo, Pageable pageable);

    long countByAtendidoFalse();
}
