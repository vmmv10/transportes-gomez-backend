package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.CotizacionEntity;
import com.transporte_gomez.erp.enums.CotizacionEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;

public interface CotizacionRepository extends JpaRepository<CotizacionEntity, Long>, JpaSpecificationExecutor<CotizacionEntity> {

    long countByEstadoIn(Collection<CotizacionEstado> estados);

    long countByTarifa_Id(Long tarifaId);
}
