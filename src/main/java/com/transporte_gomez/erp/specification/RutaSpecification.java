package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.RutaFiltro;
import com.transporte_gomez.erp.entity.RutaEntity;
import org.springframework.data.jpa.domain.Specification;

public class RutaSpecification {

    public static Specification<RutaEntity> conFiltros(RutaFiltro filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getChofer() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("chofer").get("id"), filtro.getChofer()));
            }

            if (filtro.getFechaDesde() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("fecha"), filtro.getFechaDesde()));
            }
            if (filtro.getFechaHasta() != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("fecha"), filtro.getFechaHasta()));
            }

            if (filtro.getEstado() != null && !filtro.getEstado().isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("estado"), filtro.getEstado()));
            }

            return predicates;
        };
    }
}
