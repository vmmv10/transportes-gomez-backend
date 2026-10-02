package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.ServicioTipoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServicioTipoRepository extends JpaRepository<ServicioTipoEntity, Integer> {

    List<ServicioTipoEntity> findByActivoTrueOrderByIdAsc();

    Optional<ServicioTipoEntity> findByCodigo(String codigo);
}
