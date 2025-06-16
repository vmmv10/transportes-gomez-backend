package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Bodega;
import com.transporte_gomez.erp.entity.BodegaEntity;
import org.springframework.stereotype.Service;

@Service
public class BodegaAdapter {

    public Bodega getBodega(BodegaEntity bodegaEntity) {
        Bodega bodega = new Bodega();
        bodega.setId(bodegaEntity.getId());
        bodega.setNombre(bodegaEntity.getNombre());
        bodega.setDireccion(bodegaEntity.getUbicacion());

        return bodega;
    }

    public BodegaEntity createBodega(Bodega bodega) {
        BodegaEntity bodegaEntity = new BodegaEntity();
        bodegaEntity.setNombre(bodega.getNombre());
        bodegaEntity.setUbicacion(bodega.getDireccion());

        return bodegaEntity;
    }

    public BodegaEntity updateBodega(BodegaEntity bodegaEntity, Bodega bodega) {
        bodegaEntity.setNombre(bodega.getNombre());
        bodegaEntity.setUbicacion(bodega.getDireccion());

        return bodegaEntity;
    }
}
