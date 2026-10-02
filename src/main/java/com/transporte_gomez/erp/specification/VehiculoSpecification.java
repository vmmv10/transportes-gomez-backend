package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.VehiculoFiltro;
import com.transporte_gomez.erp.entity.VehiculoEntity;
import org.springframework.data.jpa.domain.Specification;

public class VehiculoSpecification {

    public static Specification<VehiculoEntity> conFiltros(VehiculoFiltro filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getTexto() != null && !filtro.getTexto().isBlank()) {
                String texto = "%" + filtro.getTexto().trim().toLowerCase() + "%";
                predicates = cb.and(predicates, cb.or(
                        cb.like(cb.lower(root.<String>get("nombre")), texto),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("patente"), "")), texto),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("marca"), "")), texto),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("modelo"), "")), texto)));
            }
            if (filtro.getTipo() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("tipo"), filtro.getTipo()));
            }
            if (filtro.getPropiedad() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("propiedad"), filtro.getPropiedad()));
            }
            if (filtro.getEstado() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("estado"), filtro.getEstado()));
            }
            if (filtro.getActivo() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("activo"), filtro.getActivo()));
            }
            if (filtro.getProveedor() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("proveedor").get("id"), filtro.getProveedor()));
            }
            return predicates;
        };
    }
}
