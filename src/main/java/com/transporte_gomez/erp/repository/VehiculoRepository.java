package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.VehiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface VehiculoRepository extends JpaRepository<VehiculoEntity, Long>, JpaSpecificationExecutor<VehiculoEntity> {

    /** patente: ya normalizada (mayúsculas, sin espacios al borde) */
    @Query("select count(v) > 0 from VehiculoEntity v where upper(trim(v.patente)) = ?1 and (?2 is null or v.id <> ?2)")
    boolean existePatente(String patente, Long excluirId);
}
