package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.ProveedorFiltro;
import com.transporte_gomez.erp.entity.ProveedorEntity;
import org.springframework.data.jpa.domain.Specification;

public class ProveedorSpecification {

    public static Specification<ProveedorEntity> conFiltros(ProveedorFiltro filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getRut() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("rut"), filtro.getRut()));
            }

            if (filtro.getNombre() != null && !filtro.getNombre().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("razonSocial")), "%" + filtro.getNombre().toLowerCase() + "%"));
            }

            if (filtro.getActivo() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("activo"), filtro.getActivo()));
            }

            return predicates;
        };
    }
}
