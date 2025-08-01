package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.IngresosDetalle;
import com.transporte_gomez.erp.entity.IngresosDetalleEntity;
import com.transporte_gomez.erp.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngresosDetalleAdapter {

    private final ItemAdapter itemAdapter;
    private final ItemRepository itemRepository;

    public IngresosDetalle get(IngresosDetalleEntity entity){
        if (entity == null) {
            return null;
        }

        IngresosDetalle detalle = new IngresosDetalle();
        detalle.setId(entity.getId());
        detalle.setCantidad(entity.getCantidad());
        detalle.setItem(itemAdapter.getItem(entity.getItem()));

        return detalle;
    }

    public IngresosDetalleEntity create(IngresosDetalle detalle) {
        IngresosDetalleEntity entity = new IngresosDetalleEntity();
        entity.setCantidad(detalle.getCantidad());

        if (detalle.getItem() != null && detalle.getItem().getId() != null) {
            entity.setItem(itemRepository.findById(detalle.getItem().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Item no encontrado con ID: " + detalle.getItem().getId())));
        }

        return entity;
    }

    public IngresosDetalleEntity updateEntity(IngresosDetalle detalle, IngresosDetalleEntity entity) {
        if (detalle == null || entity == null) {
            return entity;
        }

        entity.setCantidad(detalle.getCantidad());

        if (detalle.getItem() != null && detalle.getItem().getId() != null) {
            entity.setItem(itemRepository.findById(detalle.getItem().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Item no encontrado con ID: " + detalle.getItem().getId())));
        }

        return entity;
    }
}
