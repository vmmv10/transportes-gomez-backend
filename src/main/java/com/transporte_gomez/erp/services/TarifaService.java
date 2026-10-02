package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.dto.Tarifa;
import com.transporte_gomez.erp.dto.TarifaFiltro;
import com.transporte_gomez.erp.dto.TarifaSugerida;
import com.transporte_gomez.erp.entity.TarifaEntity;
import com.transporte_gomez.erp.enums.TarifaPeriodo;
import com.transporte_gomez.erp.repository.CotizacionRepository;
import com.transporte_gomez.erp.repository.TarifaRepository;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TarifaService {

    private static final ZoneId CHILE = ZoneId.of("America/Santiago");

    private final TarifaRepository tarifaRepository;
    private final CotizacionRepository cotizacionRepository;
    private final ComercialReferencias ref;

    @Transactional(readOnly = true)
    public Page<Tarifa> getAll(Pageable pageable, TarifaFiltro filtro) {
        return tarifaRepository.findAll(filtros(filtro), pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Tarifa getById(Long id) {
        return toDto(buscar(id));
    }

    @Transactional
    public Tarifa create(Tarifa dto) {
        TarifaEntity entity = aplicar(dto, new TarifaEntity());
        entity.setActivo(true);
        return toDto(tarifaRepository.save(entity));
    }

    @Transactional
    public Tarifa update(Long id, Tarifa dto) {
        return toDto(tarifaRepository.save(aplicar(dto, buscar(id))));
    }

    @Transactional
    public void cambiarActivo(Long id, boolean activo) {
        TarifaEntity entity = buscar(id);
        entity.setActivo(activo);
        tarifaRepository.save(entity);
    }

    /** Solo si ninguna cotización la usó; si no, se desactiva o se cierra su vigencia. */
    @Transactional
    public void eliminar(Long id) {
        TarifaEntity entity = buscar(id);
        if (cotizacionRepository.countByTarifa_Id(id) > 0) {
            throw new IllegalArgumentException("No se puede eliminar: la tarifa se usó en cotizaciones. Cierra su vigencia o desactívala.");
        }
        tarifaRepository.delete(entity);
    }

    /**
     * Tarifa más específica para cotizar: primero la del cliente, luego la general;
     * entre ellas, la que calza con comuna de origen y destino antes que la que sirve para cualquier comuna.
     */
    @Transactional(readOnly = true)
    public TarifaSugerida sugerir(Long clienteId, Integer servicioId, Integer comunaOrigenId, Integer comunaDestinoId,
                                  Integer unidadId, BigDecimal cantidad) {
        if (servicioId == null || unidadId == null) {
            throw new IllegalArgumentException("Para buscar una tarifa indica el tipo de servicio y la unidad");
        }
        LocalDate hoy = LocalDate.now(CHILE);
        TarifaEntity mejor = tarifaRepository.candidatas(servicioId, unidadId, hoy).stream()
                .filter(t -> t.getCliente() == null || Objects.equals(t.getCliente().getId(), clienteId))
                .filter(t -> t.getComunaOrigen() == null || Objects.equals(t.getComunaOrigen().getId(), comunaOrigenId))
                .filter(t -> t.getComunaDestino() == null || Objects.equals(t.getComunaDestino().getId(), comunaDestinoId))
                .max(Comparator.comparingInt(TarifaService::especificidad).thenComparing(TarifaEntity::getVigenteDesde))
                .orElse(null);
        TarifaSugerida sugerida = new TarifaSugerida();
        if (mejor == null) {
            sugerida.setDetalle("No hay una tarifa vigente para ese servicio y unidad");
            return sugerida;
        }
        sugerida.setTarifa(toDto(mejor));
        BigDecimal cant = cantidad != null && cantidad.signum() > 0 ? cantidad : BigDecimal.ONE;
        BigDecimal monto = mejor.getPrecio().multiply(cant);
        String detalle = pesos(mejor.getPrecio()) + " × " + cant.stripTrailingZeros().toPlainString() + " "
                + mejor.getUnidadMedida().getNombre().toLowerCase();
        if (mejor.getMinimo() != null && monto.compareTo(mejor.getMinimo()) < 0) {
            monto = mejor.getMinimo();
            detalle += " (se aplica el mínimo " + pesos(mejor.getMinimo()) + ")";
        }
        sugerida.setMonto(monto.setScale(0, RoundingMode.HALF_UP));
        sugerida.setDetalle(detalle + (mejor.getCliente() != null ? " · tarifa del cliente" : " · tarifa general"));
        return sugerida;
    }

    private static int especificidad(TarifaEntity t) {
        return (t.getCliente() != null ? 4 : 0) + (t.getComunaOrigen() != null ? 2 : 0) + (t.getComunaDestino() != null ? 1 : 0);
    }

    private static String pesos(BigDecimal valor) {
        return "$" + String.format("%,d", valor.setScale(0, RoundingMode.HALF_UP).longValue()).replace(',', '.');
    }

    private TarifaEntity aplicar(Tarifa dto, TarifaEntity e) {
        if (dto.getServicioTipoId() == null) {
            throw new IllegalArgumentException("El tipo de servicio es obligatorio");
        }
        if (dto.getUnidadMedidaId() == null) {
            throw new IllegalArgumentException("La unidad de medida es obligatoria (viaje, kg, m³, bulto, pallet...)");
        }
        if (dto.getPrecio() == null || dto.getPrecio().signum() < 0) {
            throw new IllegalArgumentException("El precio es obligatorio y no puede ser negativo");
        }
        if (dto.getMinimo() != null && dto.getMinimo().signum() < 0) {
            throw new IllegalArgumentException("El cobro mínimo no puede ser negativo");
        }
        LocalDate desde = dto.getVigenteDesde() != null ? dto.getVigenteDesde() : LocalDate.now(CHILE);
        if (dto.getVigenteHasta() != null && dto.getVigenteHasta().isBefore(desde)) {
            throw new IllegalArgumentException("La vigencia hasta no puede ser anterior a la vigencia desde");
        }
        e.setCliente(ref.cliente(dto.getClienteId()));
        e.setServicioTipo(ref.servicio(dto.getServicioTipoId()));
        e.setComunaOrigen(ref.comuna(dto.getComunaOrigenId()));
        e.setComunaDestino(ref.comuna(dto.getComunaDestinoId()));
        e.setUnidadMedida(ref.unidad(dto.getUnidadMedidaId()));
        e.setPeriodo(dto.getPeriodo() != null ? dto.getPeriodo() : TarifaPeriodo.VIAJE);
        e.setPrecio(dto.getPrecio());
        e.setMinimo(dto.getMinimo());
        e.setVigenteDesde(desde);
        e.setVigenteHasta(dto.getVigenteHasta());
        e.setObservaciones(ComercialReferencias.texto(dto.getObservaciones()));
        return e;
    }

    TarifaEntity buscar(Long id) {
        return tarifaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada con ID: " + id));
    }

    Tarifa toDto(TarifaEntity e) {
        if (e == null) {
            return null;
        }
        Tarifa d = new Tarifa();
        d.setId(e.getId());
        if (e.getCliente() != null) {
            d.setClienteId(e.getCliente().getId());
            d.setClienteNombre(ComercialReferencias.nombreCliente(e.getCliente()));
        }
        d.setServicioTipoId(e.getServicioTipo().getId());
        d.setServicioTipoNombre(e.getServicioTipo().getNombre());
        if (e.getComunaOrigen() != null) {
            d.setComunaOrigenId(e.getComunaOrigen().getId());
            d.setComunaOrigenNombre(e.getComunaOrigen().getNombre());
        }
        if (e.getComunaDestino() != null) {
            d.setComunaDestinoId(e.getComunaDestino().getId());
            d.setComunaDestinoNombre(e.getComunaDestino().getNombre());
        }
        d.setUnidadMedidaId(e.getUnidadMedida().getId());
        d.setUnidadMedidaNombre(e.getUnidadMedida().getNombre());
        d.setUnidadMedidaCodigo(e.getUnidadMedida().getCodigo());
        d.setPeriodo(e.getPeriodo());
        d.setPrecio(e.getPrecio());
        d.setMinimo(e.getMinimo());
        d.setVigenteDesde(e.getVigenteDesde());
        d.setVigenteHasta(e.getVigenteHasta());
        d.setObservaciones(e.getObservaciones());
        d.setActivo(e.getActivo());
        d.setVigente(e.isVigente(LocalDate.now(CHILE)));
        return d;
    }

    private static Specification<TarifaEntity> filtros(TarifaFiltro f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (f.getCliente() != null) {
                p.add(f.getCliente() == -1 ? cb.isNull(root.get("cliente")) : cb.equal(root.get("cliente").get("id"), f.getCliente()));
            }
            if (f.getServicio() != null) {
                p.add(cb.equal(root.get("servicioTipo").get("id"), f.getServicio()));
            }
            if (f.getComuna() != null) {
                p.add(cb.or(cb.equal(root.join("comunaOrigen", JoinType.LEFT).get("id"), f.getComuna()),
                        cb.equal(root.join("comunaDestino", JoinType.LEFT).get("id"), f.getComuna())));
            }
            if (f.getActivo() != null) {
                p.add(cb.equal(root.get("activo"), f.getActivo()));
            }
            if (Boolean.TRUE.equals(f.getVigente())) {
                LocalDate hoy = LocalDate.now(CHILE);
                p.add(cb.isTrue(root.get("activo")));
                p.add(cb.lessThanOrEqualTo(root.get("vigenteDesde"), hoy));
                p.add(cb.or(cb.isNull(root.get("vigenteHasta")), cb.greaterThanOrEqualTo(root.get("vigenteHasta"), hoy)));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
    }
}
