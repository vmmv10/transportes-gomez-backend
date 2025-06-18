package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.Usuario;
import com.transporte_gomez.erp.entity.UsuarioEntity;
import org.springframework.data.jpa.domain.Specification;

public class UsuarioSpecification {
    public static Specification<UsuarioEntity> conFiltros(Usuario filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getRol() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("rol"), filtro.getRol()));
            }

            if (filtro.getNombre() != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("nombre"), filtro.getNombre()));
            }

            if (filtro.getEmail() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("email"), filtro.getEmail()));
            }

            if (filtro.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filtro.getId()));
            }

            return predicates;
        };
    }
}
