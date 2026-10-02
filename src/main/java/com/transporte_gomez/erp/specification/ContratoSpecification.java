package com.transporte_gomez.erp.specification;

import com.transporte_gomez.erp.dto.ContratoFiltro;
import com.transporte_gomez.erp.entity.ContratoEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ContratoSpecification {

    public static Specification<ContratoEntity> conFiltros(ContratoFiltro filtro) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filtro.getCliente() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("cliente").get("id"), filtro.getCliente()));
            }

            if (filtro.getTexto() != null && !filtro.getTexto().isBlank()) {
                String texto = "%" + filtro.getTexto().trim().toLowerCase() + "%";
                predicates = cb.and(predicates, cb.or(
                        cb.like(cb.lower(root.get("codigo")), texto),
                        cb.like(cb.lower(root.get("nombre")), texto)));
            }

            if (filtro.getActivo() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("activo"), filtro.getActivo()));
            }

            if (Boolean.TRUE.equals(filtro.getVigente())) {
                LocalDate hoy = LocalDate.now();
                predicates = cb.and(predicates,
                        cb.isTrue(root.get("activo")),
                        cb.or(cb.isNull(root.get("fechaInicio")), cb.lessThanOrEqualTo(root.get("fechaInicio"), hoy)),
                        cb.or(cb.isNull(root.get("fechaFin")), cb.greaterThanOrEqualTo(root.get("fechaFin"), hoy)));
            }

            return predicates;
        };
    }
}
