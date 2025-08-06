package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.OrdenServicioDetalle;
import com.transporte_gomez.erp.entity.OrdenServicioDetalleEntity;
import com.transporte_gomez.erp.services.SaldoBodegaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrdenServicioDetalleAdapter {

    private final SaldoBodegaService saldoBodegaAdapter;
    private final SaldoBodegaService saldoBodegaService;

    public OrdenServicioDetalle getOrdenServicioDetalle(OrdenServicioDetalleEntity entity) {
        OrdenServicioDetalle detalle = new OrdenServicioDetalle();
        detalle.setId(entity.getId());
        detalle.setCantidad(entity.getCantidad());
        detalle.setNombre(entity.getNombre());

        if (entity.getItem() != null) {
            detalle.setSaldoBodega(saldoBodegaService.getSaldoBodegaById(entity.getItem(), entity.getOrdenServicio().getBodega()));
        }

        return detalle;
    }

    public OrdenServicioDetalleEntity createOrdenServicioDetalle(OrdenServicioDetalle detalle) {
        OrdenServicioDetalleEntity entity = new OrdenServicioDetalleEntity();
        entity.setCantidad(detalle.getCantidad());
        entity.setNombre(detalle.getNombre());

       return entity;
    }

    public OrdenServicioDetalleEntity updateOrdenServicioDetalle(OrdenServicioDetalle detalle, OrdenServicioDetalleEntity entity) {
        entity.setCantidad(detalle.getCantidad());
        entity.setNombre(detalle.getNombre());
        return entity;
    }
}
