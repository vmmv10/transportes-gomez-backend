package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.DevolucionAdapter;
import com.transporte_gomez.erp.adapter.DevolucionDetalleAdapter;
import com.transporte_gomez.erp.adapter.UsuarioAdapter;
import com.transporte_gomez.erp.dto.*;
import com.transporte_gomez.erp.entity.DevolucionDetalleEntity;
import com.transporte_gomez.erp.entity.DevolucionEntity;
import com.transporte_gomez.erp.enums.DevolucionEstado;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipo;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipoOperacion;
import com.transporte_gomez.erp.repository.DevolucionDetalleRepository;
import com.transporte_gomez.erp.repository.DevolucionRepository;
import com.transporte_gomez.erp.specification.DevolucionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DevolucionService {
    private final DevolucionDetalleRepository devolucionDetalleRepository;
    private final DevolucionDetalleAdapter devolucionDetalleAdapter;
    private final DevolucionRepository devolucionRepository;
    private final DevolucionAdapter devolucionAdapter;
    private final ItemService itemService;
    private final SaldoBodegaService saldoBodegaService;
    private final MovimientoInventarioService movimientoInventarioService;

    public Page<Devolucion> findAll(Pageable pageable, DevolucionFiltro filtros) {
        return devolucionRepository.findAll(DevolucionSpecification.conFiltros(filtros),pageable)
                .map(devolucionEntity -> devolucionAdapter.getDto(devolucionEntity, false));

    }

    public Devolucion create(Devolucion devolucion, Usuario usuario) {
        DevolucionEntity devolucionEntity = devolucionAdapter.createDevolucion(devolucion);
        devolucionEntity.setFecha(Instant.now());
        devolucionEntity.setUser(usuario.getId());
        devolucionEntity = devolucionRepository.save(devolucionEntity);
        return devolucionAdapter.getDto(devolucionEntity, false);
    }

    public Devolucion getByFolio(Long folio) {
        DevolucionEntity devolucionEntity = devolucionRepository.findById(folio)
                .orElseThrow(() -> new IllegalArgumentException("Devolución no encontrada con folio: " + folio));
        return devolucionAdapter.getDto(devolucionEntity, true);
    }

    public DevolucionDetalle createDetalle(Long folio, String codigo) {
        DevolucionEntity devolucionEntity = devolucionRepository.getReferenceById(folio);

        Item item = itemService.getByCodigo(codigo);

        DevolucionDetalle devolucionDetalle = new DevolucionDetalle();
        devolucionDetalle.setCantidad(BigDecimal.ONE);
        devolucionDetalle.setItem(item);

        DevolucionDetalleEntity devolucionDetalleEntity = devolucionDetalleAdapter.createDevolucionDetalle(devolucionDetalle);
        devolucionDetalleEntity.setDevolucion(devolucionEntity);

        DevolucionDetalleEntity devolucionDetalleEntitySave = devolucionDetalleRepository.save(devolucionDetalleEntity);

        return devolucionDetalleAdapter.getDto(devolucionDetalleEntitySave);
    }

    public void sumarCantidadDetalle(Long detalleId, BigDecimal cantidad) {
        DevolucionDetalleEntity devolucionDetalleEntity = devolucionDetalleRepository.findById(detalleId)
                .orElseThrow(() -> new IllegalArgumentException("Detalle de devolución no encontrado con ID: " + detalleId));

        devolucionDetalleEntity.setCantidad(devolucionDetalleEntity.getCantidad().add(cantidad));
        devolucionDetalleRepository.save(devolucionDetalleEntity);
    }

    public void actualizarEstadoDevolucion(Long folio, Integer estado) {
        DevolucionEntity devolucionEntity = devolucionRepository.findById(folio)
                .orElseThrow(() -> new IllegalArgumentException("Devolución no encontrada con folio: " + folio));

        devolucionEntity.setEstado(estado);
        devolucionRepository.save(devolucionEntity);
    }

    public void updateEstado(Long folio, Integer estado) {
        DevolucionEntity devolucionEntity = devolucionRepository.findById(folio)
                .orElseThrow(() -> new IllegalArgumentException("Devolución no encontrada con folio: " + folio));

        devolucionEntity.setEstado(estado);

        if (DevolucionEstado.CERRADO.getCodigo().equals(estado)) {
            devolucionEntity.setFecha(Instant.now());
            devolucionEntity.getDevolucionesDetalles().forEach(detalle -> {
                movimientoInventarioService.create(MovimientoInventarioTipo.INGRESO, MovimientoInventarioTipoOperacion.ENTRADA, detalle.getItem().getId(), 2L, devolucionEntity.getId(), detalle.getCantidad());
                saldoBodegaService.createOrUpdate(detalle.getItem().getId(), 2l, "ENTRADA", detalle.getCantidad());
            });
        }
        devolucionRepository.save(devolucionEntity);
    }

    public void modificarCantidadDetalle(Long detalleId, BigDecimal cantidad) {
        DevolucionDetalleEntity devolucionDetalleEntity = devolucionDetalleRepository.getReferenceById(detalleId);
        if (devolucionDetalleEntity == null) {
            throw new IllegalArgumentException("Detalle de devolución no encontrado con ID: " + detalleId);
        }
        if (cantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        devolucionDetalleEntity.setCantidad(cantidad);
        devolucionDetalleRepository.save(devolucionDetalleEntity);
    }

    public void eliminarDetalle(Long detalleId) {
        DevolucionDetalleEntity devolucionDetalleEntity = devolucionDetalleRepository.findById(detalleId)
                .orElseThrow(() -> new IllegalArgumentException("Detalle de devolución no encontrado con ID: " + detalleId));

        devolucionDetalleRepository.delete(devolucionDetalleEntity);
    }

    public Devolucion getTemporalByUser(Usuario usuario) {
        List<DevolucionEntity> devolucionEntity = devolucionRepository.findByUserAndEstado(usuario.getId(), DevolucionEstado.TERMPORAL.getCodigo());
        if (devolucionEntity.isEmpty()) {
            return null;
        } else {
            return devolucionAdapter.getDto(devolucionEntity.get(0), true);
        }
    }

    public void delete(Long folio) {
        DevolucionEntity devolucionEntity = devolucionRepository.findById(folio)
                .orElseThrow(() -> new IllegalArgumentException("Devolución no encontrada con folio: " + folio));
        devolucionRepository.delete(devolucionEntity);
    }
}
