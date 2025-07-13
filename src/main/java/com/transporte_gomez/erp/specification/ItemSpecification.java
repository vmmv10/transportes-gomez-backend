package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.ItemFilter;
import com.transporte_gomez.erp.entity.ItemEntity;
import org.springframework.data.jpa.domain.Specification;

public class ItemSpecification {

    public static Specification<ItemEntity> conFiltros(ItemFilter filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getNombre() != null && !filtro.getNombre().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("nombre")), "%" + filtro.getNombre().toLowerCase() + "%"));
            }

            if (filtro.getActivo() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("activo"), filtro.getActivo()));
            }

            if (filtro.getDescripcion() != null && !filtro.getDescripcion().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("descripcion")), "%" + filtro.getDescripcion().toLowerCase() + "%"));
            }

            if (filtro.getCategoria() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("categoria").get("id"), filtro.getCategoria()));
            }

            if (filtro.getMarca() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("marca").get("id"), filtro.getMarca()));
            }

            if (filtro.getUnidadMedida() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("unidadMedida").get("id"), filtro.getUnidadMedida()));
            }

            return predicates;
        };
    }
}
