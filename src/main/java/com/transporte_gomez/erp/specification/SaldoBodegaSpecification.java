package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.SaldoBodegaFiltro;
import com.transporte_gomez.erp.entity.SaldosBodegaEntity;
import org.springframework.data.jpa.domain.Specification;

public class SaldoBodegaSpecification {

    public static Specification<SaldosBodegaEntity> conFiltros(SaldoBodegaFiltro filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getBodega() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("bodega").get("id"), filtro.getBodega()));
            }

            if (filtro.getItemId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("item").get("id"), filtro.getItemId()));
            }

            if (filtro.getMarca() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("item").get("marca"), filtro.getMarca()));
            }

            if (filtro.getCategoria() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("item").get("categoria"), filtro.getCategoria()));
            }

            if (filtro.getNombre() != null) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("item").get("nombre")), "%" + filtro.getNombre().toLowerCase() + "%"));
            }

            return predicates;
        };
    }
}
