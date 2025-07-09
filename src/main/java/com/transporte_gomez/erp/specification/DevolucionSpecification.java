package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.DevolucionFiltro;
import com.transporte_gomez.erp.entity.DevolucionEntity;
import org.springframework.data.jpa.domain.Specification;

public class DevolucionSpecification {
    public static Specification<DevolucionEntity> conFiltros(DevolucionFiltro filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filtro.getId()));
            }

            if (filtro.getEscuela() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("escuela").get("id"), filtro.getEscuela()));
            }

            if (filtro.getOrden() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("orden").get("id"), filtro.getOrden()));
            }

            return predicates;
        };
    }
}
