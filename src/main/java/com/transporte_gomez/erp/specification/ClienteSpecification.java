package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.ClienteFiltro;
import com.transporte_gomez.erp.entity.ClienteEntity;
import org.springframework.data.jpa.domain.Specification;

public class ClienteSpecification {

    public static Specification<ClienteEntity> conFiltros(ClienteFiltro filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getNombre() != null && !filtro.getNombre().isBlank()) {
                String texto = "%" + filtro.getNombre().trim().toLowerCase() + "%";
                predicates = cb.and(predicates, cb.or(
                        cb.like(cb.lower(root.get("razonSocial")), texto),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("nombreCorto"), "")), texto)));
            }

            if (filtro.getRut() != null && !filtro.getRut().isBlank()) {
                String rut = filtro.getRut().replace(".", "").replace(" ", "").toUpperCase();
                predicates = cb.and(predicates, cb.like(root.get("rut"), "%" + rut + "%"));
            }

            if (filtro.getSector() != null && !filtro.getSector().isBlank()) {
                predicates = cb.and(predicates, cb.equal(root.get("sector"), filtro.getSector()));
            }

            if (filtro.getActivo() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("activo"), filtro.getActivo()));
            }

            return predicates;
        };
    }
}
