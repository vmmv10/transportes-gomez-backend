package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.IngresosAdapter;
import com.transporte_gomez.erp.adapter.IngresosDetalleAdapter;
import com.transporte_gomez.erp.dto.*;
import com.transporte_gomez.erp.dto.Ingresos;
import com.transporte_gomez.erp.entity.IngresosDetalleEntity;
import com.transporte_gomez.erp.entity.IngresosEntity;
import com.transporte_gomez.erp.entity.OrdenServicioDetalleEntity;
import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import com.transporte_gomez.erp.enums.IngresoEstado;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipo;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipoOperacion;
import com.transporte_gomez.erp.repository.IngresosDetalleRepository;
import com.transporte_gomez.erp.repository.IngresosRepository;
import com.transporte_gomez.erp.specification.IngresosSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class IngresoService {
    private final IngresosAdapter ingresosAdapter;
    private final IngresosDetalleAdapter ingresosDetalleAdapter;
    private final IngresosDetalleRepository ingresosDetalleRepository;
    private final IngresosRepository ingresosRepository;
    private final ItemService itemService;
    private final SaldoBodegaService saldoBodegaService;
    private final MovimientoInventarioService movimientoInventarioService;

    public Page<Ingresos> findAll(Pageable pageable, IngresosFiltro filtros) {
        return ingresosRepository.findAll(IngresosSpecification.conFiltros(filtros),pageable)
                .map(ingresoEntity -> ingresosAdapter.toDto(ingresoEntity, false));

    }

    public Ingresos create(Ingresos ingresoEmergencia, Usuario usuario) {
        IngresosEntity ingresoEntity = ingresosAdapter.toEntity(ingresoEmergencia);
        ingresoEntity.setFecha(Instant.now());
        ingresoEntity.setUser(usuario.getId());
        ingresoEntity = ingresosRepository.save(ingresoEntity);
        return ingresosAdapter.toDto(ingresoEntity, false);
    }

    public Ingresos getByFolio(Integer folio) {
        IngresosEntity ingresoEntity = ingresosRepository.findById(folio)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado con folio: " + folio));
        return ingresosAdapter.toDto(ingresoEntity, true);
    }

    public IngresosDetalle createDetalle(Integer folio, String codigo) {
        IngresosEntity ingresoEntity = ingresosRepository.getReferenceById(folio);

        Item item = itemService.getByCodigo(codigo);

        IngresosDetalle ingresoDetalle = new IngresosDetalle();
        ingresoDetalle.setCantidad(BigDecimal.ZERO);
        ingresoDetalle.setItem(item);

        IngresosDetalleEntity ingresoDetalleEntity = ingresosDetalleAdapter.create(ingresoDetalle);
        ingresoDetalleEntity.setIngreso(ingresoEntity);

        IngresosDetalleEntity ingresoDetalleEntitySave = ingresosDetalleRepository.save(ingresoDetalleEntity);

        return ingresosDetalleAdapter.get(ingresoDetalleEntitySave);
    }

    public void sumarCantidadDetalle(Integer detalleId, BigDecimal cantidad) {
        IngresosDetalleEntity ingresoDetalleEntity = ingresosDetalleRepository.findById(detalleId)
                .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado con ID: " + detalleId));

        ingresoDetalleEntity.setCantidad(ingresoDetalleEntity.getCantidad().add(cantidad));
        ingresosDetalleRepository.save(ingresoDetalleEntity);
    }

    public void actualizarEstado(Integer folio, Integer estado) {
        IngresosEntity ingresoEntity = ingresosRepository.findById(folio)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrada con folio: " + folio));

        ingresoEntity.setEstado(estado);
        ingresosRepository.save(ingresoEntity);
    }

    public void updateEstado(Integer folio, Integer estado) {
        IngresosEntity ingresoEntity = ingresosRepository.findById(folio)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrada con folio: " + folio));

        ingresoEntity.setEstado(estado);

        if (IngresoEstado.CERRADO.getCodigo().equals(estado)) {
            ingresoEntity.setFechaCierre(LocalDateTime.now());
            ingresoEntity.getDetalles().forEach(detalle -> {
                movimientoInventarioService.create(MovimientoInventarioTipo.INGRESO, MovimientoInventarioTipoOperacion.ENTRADA, detalle.getItem().getId(), ingresoEntity.getBodega().getId(), Long.valueOf(folio), detalle.getCantidad());
                saldoBodegaService.createOrUpdate(detalle.getItem().getId(), ingresoEntity.getBodega().getId(), "ENTRADA", detalle.getCantidad());
            });
        }
        ingresosRepository.save(ingresoEntity);
    }

    public void modificarCantidadDetalle(Integer detalleId, BigDecimal cantidad) {
        IngresosDetalleEntity ingresoDetalleEntity = ingresosDetalleRepository.getReferenceById(detalleId);
        if (cantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        ingresoDetalleEntity.setCantidad(cantidad);
        ingresosDetalleRepository.save(ingresoDetalleEntity);
    }

    public void eliminarDetalle(Integer detalleId) {
        IngresosDetalleEntity ingresoDetalleEntity = ingresosDetalleRepository.findById(detalleId)
                .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado con ID: " + detalleId));

        ingresosDetalleRepository.delete(ingresoDetalleEntity);
    }

    public Ingresos getTemporalByUser(Usuario usuario) {
        List<IngresosEntity> ingresoEntity = ingresosRepository.findByUserAndEstado(usuario.getId(), IngresoEstado.TERMPORAL.getCodigo());
        if (ingresoEntity.isEmpty()) {
            return null;
        }
        return ingresosAdapter.toDto(ingresoEntity.get(0), true);
    }

    public void delete(Integer folio) {
        IngresosEntity ingresoEntity = ingresosRepository.findById(folio)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado con folio: " + folio));
        ingresosRepository.delete(ingresoEntity);
    }

    public void RestarSaldo(OrdenServicioEntity ordenServicioEntity) {
        IngresosEntity ingresoEntity = ingresosRepository.findById(ordenServicioEntity.getIngreso())
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado con folio: " + ordenServicioEntity.getIngreso()));

        for (IngresosDetalleEntity detalle : ingresoEntity.getDetalles()) {
            for (OrdenServicioDetalleEntity detalleOs : ordenServicioEntity.getDetalles()) {
                if (detalle.getItem().getId().equals(detalleOs.getItem())) {
                    BigDecimal nuevaCantidad = detalle.getSaldo().subtract(detalleOs.getCantidad());
                    detalle.setSaldo(nuevaCantidad.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : nuevaCantidad);
                }
            }
        }

        ingresosRepository.save(ingresoEntity);
    }

    public void sumarSaldo(OrdenServicioEntity ordenServicioEntity) {
        IngresosEntity ingresoEntity = ingresosRepository.findById(ordenServicioEntity.getIngreso())
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado con folio: " + ordenServicioEntity.getIngreso()));

        for (IngresosDetalleEntity detalle : ingresoEntity.getDetalles()) {
            for (OrdenServicioDetalleEntity detalleOs : ordenServicioEntity.getDetalles()) {
                if (detalle.getItem().getId().equals(detalleOs.getItem())) {
                    BigDecimal nuevaCantidad = detalle.getSaldo().add(detalleOs.getCantidad());
                    detalle.setSaldo(nuevaCantidad.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : nuevaCantidad);
                }
            }
        }

        ingresosRepository.save(ingresoEntity);
    }
}
