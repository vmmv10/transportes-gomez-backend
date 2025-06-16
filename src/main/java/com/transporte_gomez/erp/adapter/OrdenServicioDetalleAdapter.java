package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.OrdenServicioDetalle;
import com.transporte_gomez.erp.entity.OrdenServicioDetalleEntity;
import org.springframework.stereotype.Service;

@Service

public class OrdenServicioDetalleAdapter {

    public OrdenServicioDetalle getOrdenServicioDetalle(OrdenServicioDetalleEntity entity) {
        OrdenServicioDetalle detalle = new OrdenServicioDetalle();
        detalle.setId(entity.getId());
        detalle.setCantidad(entity.getCantidad());
        detalle.setNombre(entity.getNombre());
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
