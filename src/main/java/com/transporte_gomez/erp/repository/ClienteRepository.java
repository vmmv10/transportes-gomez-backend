package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long>, JpaSpecificationExecutor<ClienteEntity> {

    Optional<ClienteEntity> findByRut(String rut);

    List<ClienteEntity> findByActivoTrueOrderByRazonSocialAsc();
}
