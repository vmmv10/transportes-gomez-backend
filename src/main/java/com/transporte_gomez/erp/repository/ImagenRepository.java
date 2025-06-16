package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.ImagenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImagenRepository extends JpaRepository<ImagenEntity, Long> {
    @Query("select i from ImagenEntity i where i.entidadTipo = ?1 and i.entidadId = ?2")
    List<ImagenEntity> findByEntidadTipoAndEntidadId(Integer entidadTipo, Long entidadId);
}