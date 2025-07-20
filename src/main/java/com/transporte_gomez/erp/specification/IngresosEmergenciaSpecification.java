package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.IngresosEmergenciaFiltro;
import com.transporte_gomez.erp.entity.IngresosEmergenciaEntity;
import org.springframework.data.jpa.domain.Specification;

public class IngresosEmergenciaSpecification {
    public static Specification<IngresosEmergenciaEntity> conFiltros(IngresosEmergenciaFiltro filtro){
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

            return predicates;
        };
    }
}
