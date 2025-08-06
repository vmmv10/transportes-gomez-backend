package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.OrdenServicioDetalleAdapter;
import com.transporte_gomez.erp.dto.OrdenServicioDetalle;
import com.transporte_gomez.erp.entity.OrdenServicioDetalleEntity;
import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipo;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipoOperacion;
import com.transporte_gomez.erp.repository.OrdenServicioDetalleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrdenServicioDetalleService {

    private final OrdenServicioDetalleRepository ordenServicioDetalleRepository;
    private final OrdenServicioDetalleAdapter ordenServicioDetalleAdapter;
    private final MovimientoInventarioService movimientoInventarioService;
    private final SaldoBodegaService saldoBodegaService;

    public void create(List<OrdenServicioDetalle> ordenServicioDetalleList, OrdenServicioEntity ordenServicioEntitySave) {
        for (OrdenServicioDetalle detalle : ordenServicioDetalleList) {
            OrdenServicioDetalleEntity detalleEntity = ordenServicioDetalleAdapter.createOrdenServicioDetalle(detalle);
            detalleEntity.setOrdenServicio(ordenServicioEntitySave);
            if (detalle.getSaldoBodega() != null && detalle.getSaldoBodega().getItem() != null) {
                detalleEntity.setItem(detalle.getSaldoBodega().getItem().getId());
            }
            detalleEntity = ordenServicioDetalleRepository.save(detalleEntity);
            if (ordenServicioEntitySave.getBodega() != null && ordenServicioEntitySave.getBodega() != 4L) {
                movimientoInventarioService.create(MovimientoInventarioTipo.ORDEN_SERVICIO, MovimientoInventarioTipoOperacion.SALIDA, detalleEntity.getItem(), ordenServicioEntitySave.getBodega(), ordenServicioEntitySave.getId(), detalleEntity.getCantidad());
                saldoBodegaService.createOrUpdate(detalle.getSaldoBodega().getItem().getId(), ordenServicioEntitySave.getBodega(), "SALIDA", detalleEntity.getCantidad());
            }
        }
    }

    @Transactional
    public void update(List<OrdenServicioDetalle> ordenServicioDetalleList, OrdenServicioEntity ordenServicioEntity) {
        if (ordenServicioDetalleList == null) {
            throw new IllegalArgumentException("Lista de detalles no puede ser nula");
        }

        List<OrdenServicioDetalleEntity> nuevosDetalles = new ArrayList<>();
        List<OrdenServicioDetalleEntity> detallesActuales = ordenServicioEntity.getDetalles();

        for (OrdenServicioDetalle detalle : ordenServicioDetalleList) {
            OrdenServicioDetalleEntity detalleEntity;

            if (detalle.getId() == null || detalle.getId() <= 0) {
                detalleEntity = ordenServicioDetalleAdapter.createOrdenServicioDetalle(detalle);
                if (detalle.getSaldoBodega() != null && detalle.getSaldoBodega().getItem() != null) {
                    detalleEntity.setItem(detalle.getSaldoBodega().getItem().getId());
                    movimientoInventarioService.create(
                            MovimientoInventarioTipo.ORDEN_SERVICIO,
                            MovimientoInventarioTipoOperacion.SALIDA,
                            detalle.getSaldoBodega().getItem().getId(),
                            ordenServicioEntity.getBodega(),
                            ordenServicioEntity.getId(),
                            detalle.getCantidad()
                    );
                    saldoBodegaService.createOrUpdate(detalle.getSaldoBodega().getItem().getId(), ordenServicioEntity.getBodega(), "SALIDA", detalleEntity.getCantidad());
                }
            } else {
                detalleEntity = ordenServicioDetalleRepository.findById(detalle.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado con ID: " + detalle.getId()));

                detalleEntity = ordenServicioDetalleAdapter.updateOrdenServicioDetalle(detalle, detalleEntity);

                if (debeActualizarInventario(ordenServicioEntity.getBodega())) {
                    movimientoInventarioService.modificarCantidad(
                            MovimientoInventarioTipo.ORDEN_SERVICIO,
                            detalleEntity.getItem(),
                            ordenServicioEntity.getBodega(),
                            ordenServicioEntity.getId(),
                            detalleEntity.getCantidad()
                    );
                }
            }

            detalleEntity.setOrdenServicio(ordenServicioEntity);
            nuevosDetalles.add(detalleEntity);
        }

        ordenServicioDetalleRepository.saveAll(nuevosDetalles);
    }

    private boolean debeActualizarInventario(Long bodegaId) {
        return bodegaId != null && !Objects.equals(bodegaId, 4L);
    }

    @Transactional
    public void delete(Long id) {
       OrdenServicioDetalleEntity ordenServicioDetalleEntity = ordenServicioDetalleRepository.getReferenceById(id);

        if (debeActualizarInventario(ordenServicioDetalleEntity.getOrdenServicio().getBodega())) {
            movimientoInventarioService.deleteMovimiento(
                    MovimientoInventarioTipo.ORDEN_SERVICIO,
                    ordenServicioDetalleEntity.getItem(),
                    ordenServicioDetalleEntity.getOrdenServicio().getBodega(),
                    ordenServicioDetalleEntity.getOrdenServicio().getId()
            );
        }
        ordenServicioDetalleRepository.delete(ordenServicioDetalleEntity);
    }
}
