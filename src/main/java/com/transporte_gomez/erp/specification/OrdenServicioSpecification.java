package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.OrdenServicioFiltro;
import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import org.springframework.data.jpa.domain.Specification;

public class OrdenServicioSpecification {

    public static Specification<OrdenServicioEntity> conFiltros(OrdenServicioFiltro filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getEscuelaId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("escuela").get("id"), filtro.getEscuelaId()));
            }

            if (filtro.getProveedorId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("proveedor").get("id"), filtro.getProveedorId()));
            }

            if (filtro.getFecha() != null && !filtro.getFecha().isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("fecha"), filtro.getFecha()));
            }

            if(filtro.getEnRuta() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("enRuta"), filtro.getEnRuta()));
            }

            if (filtro.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filtro.getId()));
            }

            if (filtro.getEntregado() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("entregado"), filtro.getEntregado()));
            }

            return predicates;
        };
    }
}
