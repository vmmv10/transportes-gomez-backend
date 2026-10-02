package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.DestinoFiltro;
import com.transporte_gomez.erp.entity.DestinoEntity;
import org.springframework.data.jpa.domain.Specification;

public class DestinoSpecification {

    public static Specification<DestinoEntity> conFiltros(DestinoFiltro filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getNombre() != null && !filtro.getNombre().isBlank()) {
                String texto = "%" + filtro.getNombre().trim().toLowerCase() + "%";
                predicates = cb.and(predicates, cb.or(
                        cb.like(cb.lower(root.get("nombre")), texto),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("direccion"), "")), texto)));
            }

            if (filtro.getTipo() != null && !filtro.getTipo().isBlank()) {
                predicates = cb.and(predicates, cb.equal(root.get("tipo"), filtro.getTipo()));
            }

            if (filtro.getComuna() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("comuna").get("id"), filtro.getComuna()));
            }

            if (filtro.getCliente() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("cliente").get("id"), filtro.getCliente()));
            }

            if (filtro.getActivo() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("activo"), filtro.getActivo()));
            }

            return predicates;
        };
    }
}
