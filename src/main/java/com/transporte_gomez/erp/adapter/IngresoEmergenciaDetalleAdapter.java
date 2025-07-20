package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.IngresosEmergenciaDetalle;
import com.transporte_gomez.erp.entity.IngresosEmergenciaDetalleEntity;
import com.transporte_gomez.erp.entity.IngresosEmergenciaEntity;
import com.transporte_gomez.erp.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngresoEmergenciaDetalleAdapter {

    private final ItemAdapter itemAdapter;
    private final ItemRepository itemRepository;

    public IngresosEmergenciaDetalle get(IngresosEmergenciaDetalleEntity entity){
        if (entity == null) {
            return null;
        }

        IngresosEmergenciaDetalle detalle = new IngresosEmergenciaDetalle();
        detalle.setId(entity.getId());
        detalle.setCantidad(entity.getCantidad());
        detalle.setItem(itemAdapter.getItem(entity.getItem()));

        return detalle;
    }

    public IngresosEmergenciaDetalleEntity create(IngresosEmergenciaDetalle detalle) {
        IngresosEmergenciaDetalleEntity entity = new IngresosEmergenciaDetalleEntity();
        entity.setCantidad(detalle.getCantidad());

        if (detalle.getItem() != null && detalle.getItem().getId() != null) {
            entity.setItem(itemRepository.findById(detalle.getItem().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Item no encontrado con ID: " + detalle.getItem().getId())));
        }

        return entity;
    }

    public IngresosEmergenciaDetalleEntity updateEntity(IngresosEmergenciaDetalle detalle, IngresosEmergenciaDetalleEntity entity) {
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
