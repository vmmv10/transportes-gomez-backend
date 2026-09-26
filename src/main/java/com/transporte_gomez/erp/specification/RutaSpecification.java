package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.RutaFiltro;
import com.transporte_gomez.erp.entity.RutaEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class RutaSpecification {

    public static Specification<RutaEntity> conFiltros(RutaFiltro filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filtro.getId()));
            }

            if (filtro.getChofer() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("chofer").get("id"), filtro.getChofer()));
            }

            if (filtro.getFechaDesde() != null && !filtro.getFechaDesde().isBlank()) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.<LocalDate>get("fecha"), parseFecha(filtro.getFechaDesde())));
            }
            if (filtro.getFechaHasta() != null && !filtro.getFechaHasta().isBlank()) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.<LocalDate>get("fecha"), parseFecha(filtro.getFechaHasta())));
            }

            if (filtro.getEstado() != null && !filtro.getEstado().isEmpty()) {
                predicates = cb.and(predicates, cb.equal(root.get("estado"), filtro.getEstado()));
            }

            if (filtro.getFecha() != null && !filtro.getFecha().isBlank()) {
                predicates = cb.and(predicates, cb.equal(root.get("fecha"), parseFecha(filtro.getFecha())));
            }

            if (filtro.getConductor() != null &&  filtro.getConductor()) {
                predicates = cb.and(predicates, cb.equal(root.get("chofer").get("id"), filtro.getChofer()));
            }

            return predicates;
        };
    }

    /** Acepta "yyyy-MM-dd" o un ISO completo ("2026-09-25T03:00:00.000Z"): se toma la parte de la fecha. */
    private static LocalDate parseFecha(String valor) {
        String v = valor.trim();
        return LocalDate.parse(v.length() > 10 ? v.substring(0, 10) : v);
    }
}
