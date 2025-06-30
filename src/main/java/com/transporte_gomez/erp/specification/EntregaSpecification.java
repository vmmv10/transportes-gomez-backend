package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.EntregaFiltro;
import com.transporte_gomez.erp.dto.EscuelaFilter;
import com.transporte_gomez.erp.entity.EntregaEntity;
import com.transporte_gomez.erp.entity.EscuelaEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
public class EntregaSpecification {

    public static Specification<EntregaEntity> conFiltros(EntregaFiltro filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getId() != null) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("nombre")), "%" + filtro.getId()));
            }

            if (filtro.getEntregado() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("entregado"), filtro.getEntregado()));
            }

            if (filtro.getFecha() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("fecha"), filtro.getFecha()));
            }

            if (filtro.getOrdenServicio() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("ordenServicio").get("id"), filtro.getOrdenServicio()));
            }

            if (filtro.getEscuela() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("ordenServicio").get("escuela").get("id"), filtro.getEscuela()));
            }

            return predicates;
        };
    }
}
