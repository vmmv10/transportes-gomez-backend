package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.MarcaFiltro;
import com.transporte_gomez.erp.entity.MarcaEntity;
import org.springframework.data.jpa.domain.Specification;

public class MarcaSpecification {
    public static Specification<MarcaEntity> conFiltros(MarcaFiltro filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getNombre() != null && !filtro.getNombre().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("nombre")), "%" + filtro.getNombre().toLowerCase() + "%"));
            }

            if (filtro.getActivo() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("activo"), filtro.getActivo()));
            }

            return predicates;
        };
    }
}
