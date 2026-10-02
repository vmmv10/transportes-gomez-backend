package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.ComunaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComunaRepository extends JpaRepository<ComunaEntity, Integer> {

    List<ComunaEntity> findByActivoTrueOrderByNombreAsc();

    Optional<ComunaEntity> findFirstByNombreIgnoreCase(String nombre);
}
