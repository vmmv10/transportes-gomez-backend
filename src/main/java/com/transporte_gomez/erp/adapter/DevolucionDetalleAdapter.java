package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.DevolucionDetalle;
import com.transporte_gomez.erp.entity.DevolucionDetalleEntity;
import com.transporte_gomez.erp.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DevolucionDetalleAdapter {

    private final ItemAdapter itemAdapter;
    private final ItemRepository itemRepository;

    public DevolucionDetalle getDto(DevolucionDetalleEntity devolucionDetalleEntity) {
        DevolucionDetalle devolucionDetalle = new DevolucionDetalle();
        devolucionDetalle.setId(devolucionDetalleEntity.getId());
        devolucionDetalle.setCantidad(devolucionDetalleEntity.getCantidad());
        devolucionDetalle.setItem(itemAdapter.getItem(devolucionDetalleEntity.getItem()));

        return devolucionDetalle;
    }

    public DevolucionDetalleEntity get(DevolucionDetalle devolucionDetalle) {
        DevolucionDetalleEntity devolucionDetalleEntity = new DevolucionDetalleEntity();
        devolucionDetalleEntity.setId(devolucionDetalle.getId());
        devolucionDetalleEntity.setCantidad(devolucionDetalle.getCantidad());
        devolucionDetalleEntity.setItem(itemRepository.getReferenceById(devolucionDetalle.getItem().getId()));

        return devolucionDetalleEntity;
    }

    public DevolucionDetalleEntity createDevolucionDetalle(DevolucionDetalle devolucionDetalle) {
        DevolucionDetalleEntity devolucionDetalleEntity = new DevolucionDetalleEntity();
        devolucionDetalleEntity.setCantidad(devolucionDetalle.getCantidad());
        devolucionDetalleEntity.setItem(itemRepository.getReferenceById(devolucionDetalle.getItem().getId()));
        return devolucionDetalleEntity;
    }
}
