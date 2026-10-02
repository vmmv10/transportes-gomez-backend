package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.DestinoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DestinoRepository extends JpaRepository<DestinoEntity, Long>, JpaSpecificationExecutor<DestinoEntity> {

    Optional<DestinoEntity> findByEscuela_Id(Long escuelaId);
}
