package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.ContratoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContratoRepository extends JpaRepository<ContratoEntity, Long>, JpaSpecificationExecutor<ContratoEntity> {

    boolean existsByCliente_IdAndCodigoIgnoreCase(Long clienteId, String codigo);

    boolean existsByCliente_IdAndCodigoIgnoreCaseAndIdNot(Long clienteId, String codigo, Long id);
}
