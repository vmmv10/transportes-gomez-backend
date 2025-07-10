package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.DevolucionAdapter;
import com.transporte_gomez.erp.adapter.DevolucionDetalleAdapter;
import com.transporte_gomez.erp.dto.Devolucion;
import com.transporte_gomez.erp.dto.DevolucionDetalle;
import com.transporte_gomez.erp.dto.DevolucionFiltro;
import com.transporte_gomez.erp.dto.Item;
import com.transporte_gomez.erp.entity.DevolucionDetalleEntity;
import com.transporte_gomez.erp.entity.DevolucionEntity;
import com.transporte_gomez.erp.repository.DevolucionDetalleRepository;
import com.transporte_gomez.erp.repository.DevolucionRepository;
import com.transporte_gomez.erp.specification.DevolucionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class DevolucionService {
    private final DevolucionDetalleRepository devolucionDetalleRepository;
    private final DevolucionDetalleAdapter devolucionDetalleAdapter;
    private final DevolucionRepository devolucionRepository;
    private final DevolucionAdapter devolucionAdapter;
    private final ItemService itemService;

    public Page<Devolucion> findAll(Pageable pageable, DevolucionFiltro filtros) {
        return devolucionRepository.findAll(DevolucionSpecification.conFiltros(filtros),pageable)
                .map(devolucionEntity -> devolucionAdapter.getDto(devolucionEntity, false));

    }

    public Devolucion create(Devolucion devolucion) {
        DevolucionEntity devolucionEntity = devolucionAdapter.createDevolucion(devolucion);
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

        if (devolucionEntity == null) {
            throw new IllegalArgumentException("Devolución no encontrada con folio: " + folio);
        }

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
}
