package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.RutaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RutaRepository extends JpaRepository<RutaEntity, Integer> {
    Page<RutaEntity> findAll(Specification<RutaEntity> rutaEntitySpecification, Pageable pageable);

    @Query("select r from RutaEntity r where r.fecha = ?1 and r.chofer.id = ?2")
    Optional<RutaEntity> findByFechaAndChofer_Id(LocalDate fecha, Long id);

    @Query("select r from RutaEntity r where r.chofer.id = ?1 and r.fecha = ?2 and r.estado = ?3 order by  r.fecha desc ")
    List<RutaEntity> findByChofer_IdAndFechaAndEstado(Long id, LocalDate fecha, String estado);
}