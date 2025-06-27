package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.EntregaEntity;
import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import com.transporte_gomez.erp.entity.RutaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EntregaRepository extends JpaRepository<EntregaEntity, Integer> {
    @Query("select e from EntregaEntity e where e.ruta.id = ?1")
    List<EntregaEntity> findByRuta_Id(Integer id);

    Page<EntregaEntity> findAll(Specification<EntregaEntity> spec, Pageable pageable);

    @Transactional
    @Modifying
    @Query("delete from EntregaEntity e where e.ruta.id = ?1 and e.ordenServicio.id = ?2")
    void deleteByRutaAndOrdenServicio(Long ruta, Long ordenServicio);

    @Query("select e from EntregaEntity e where e.ruta.id = ?1 and e.ordenServicio.id = ?2")
    Optional<EntregaEntity> findByRuta_IdAndOrdenServicio_Id(Integer id, Long id1);

    @Query(value = """
        SELECT TO_CHAR(e.creado_en, 'YYYY-MM') AS mes, COUNT(*) AS total
        FROM qa.entregas e
        WHERE e.entregado = true
        GROUP BY TO_CHAR(e.creado_en, 'YYYY-MM')
        ORDER BY mes
    """, nativeQuery = true)
    List<Object[]> contarEntregasEntregadasPorMes();
}