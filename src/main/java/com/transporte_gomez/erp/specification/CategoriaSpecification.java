package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.entity.CategoriaEntity;
import org.springframework.data.jpa.domain.Specification;

public class CategoriaSpecification {

    public static Specification<CategoriaEntity> conFiltros(String nombre, Boolean activo) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (nombre != null && !nombre.isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }

            if (activo != null) {
                predicates = cb.and(predicates, cb.equal(root.get("activo"), activo));
            }

            return predicates;
        };
    }
}
