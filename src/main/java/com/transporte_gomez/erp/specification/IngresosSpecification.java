package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.IngresosFiltro;
import com.transporte_gomez.erp.entity.IngresosEntity;
import org.springframework.data.jpa.domain.Specification;

public class IngresosSpecification {
    public static Specification<IngresosEntity> conFiltros(IngresosFiltro filtro){
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getFechaDesde() != null && !filtro.getFechaDesde().isEmpty()) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("fecha"), filtro.getFechaDesde()));
            }

            if (filtro.getFechaHasta() != null && !filtro.getFechaHasta().isEmpty()) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("fecha"), filtro.getFechaHasta()));
            }

            if (filtro.getDocumento() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("documento"), filtro.getDocumento()));
            }

            if (filtro.getBodega() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("bodega").get("id"), filtro.getBodega()));
            }

            return predicates;
        };
    }
}
