package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.DocumentoFiltro;
import com.transporte_gomez.erp.entity.DocumentoEntity;
import org.springframework.data.jpa.domain.Specification;

public class DocumentoSpecification {
    public static Specification<DocumentoEntity> conFiltros(DocumentoFiltro filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getUsuarioId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("usuario").get("id"), filtro.getUsuarioId()));
            }

            if (filtro.getNumero() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("numero"), filtro.getNumero()));
            }

            if (filtro.getTipoCodigo() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("tipoCodigo"), filtro.getTipoCodigo()));
            }

            if (filtro.getTipoNombre() != null && !filtro.getTipoNombre().isEmpty()) {
                predicates = cb.and(predicates, cb.like(cb.lower(root.get("tipoNombre")), "%" + filtro.getTipoNombre().toLowerCase() + "%"));
            }

            if (filtro.getFechaCreacionDesde() != null && !filtro.getFechaCreacionDesde().isEmpty()) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("fechaCreacion"), filtro.getFechaCreacionDesde()));
            }

            if (filtro.getFechaCreacionHasta() != null && !filtro.getFechaCreacionHasta().isEmpty()) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("fechaCreacion"), filtro.getFechaCreacionHasta()));
            }

            if (filtro.getProveedor() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("proveedor").get("id"), filtro.getProveedor()));
            }

            if (filtro.getEscuela() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("escuela").get("id"), filtro.getEscuela()));
            }

            return predicates;
        };
    }
}
