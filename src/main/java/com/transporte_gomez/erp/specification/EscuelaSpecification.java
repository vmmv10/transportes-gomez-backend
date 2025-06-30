package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.EscuelaFilter;
import com.transporte_gomez.erp.entity.EscuelaEntity;
import org.springframework.data.jpa.domain.Specification;

public class EscuelaSpecification {

    public static Specification<EscuelaEntity> conFiltros(EscuelaFilter filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getNombre() != null && !filtro.getNombre().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("nombre")), "%" + filtro.getNombre().toLowerCase() + "%"));
            }

            if (filtro.getComuna() != null && !filtro.getComuna().isEmpty()) {
                predicates = cb.and(predicates, cb.equal(cb.lower(root.get("comuna")), filtro.getComuna().toLowerCase()));
            }

            if (filtro.getRbd() != null && !filtro.getRbd().isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("rbd"), filtro.getRbd()));
            }

            if (filtro.getDirector() != null && !filtro.getDirector().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("director")), "%" + filtro.getDirector().toLowerCase() + "%"));
            }

            if(filtro.getActivo() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("activo"), filtro.getActivo()));
            }

            return predicates;
        };
    }
}
