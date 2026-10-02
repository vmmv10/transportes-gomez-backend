package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.TarifaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TarifaRepository extends JpaRepository<TarifaEntity, Long>, JpaSpecificationExecutor<TarifaEntity> {

    /** Candidatas para cotizar: activas, vigentes en la fecha, del servicio y unidad (se elige la más específica en el servicio). */
    @Query("""
            select t from TarifaEntity t
            where t.activo = true
              and t.servicioTipo.id = ?1
              and t.unidadMedida.id = ?2
              and t.vigenteDesde <= ?3
              and (t.vigenteHasta is null or t.vigenteHasta >= ?3)
            """)
    List<TarifaEntity> candidatas(Integer servicioTipoId, Integer unidadMedidaId, LocalDate fecha);

    long countByCliente_Id(Long clienteId);
}
