package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.OrdenServicioCategoria;
import com.transporte_gomez.erp.entity.OrdenesServiciosCategoriaEntity;
import org.springframework.stereotype.Service;

@Service
public class OrdenServicioCategoriaAdapter {

    public OrdenesServiciosCategoriaEntity createCategoria(OrdenServicioCategoria ordenServicioCategoria) {
        OrdenesServiciosCategoriaEntity ordenesServiciosCategoriaEntity = new OrdenesServiciosCategoriaEntity();
        ordenesServiciosCategoriaEntity.setNombre(ordenServicioCategoria.getNombre());
        ordenesServiciosCategoriaEntity.setActivo(true);

        return ordenesServiciosCategoriaEntity;
    }

    public OrdenServicioCategoria toDTO(OrdenesServiciosCategoriaEntity entity) {
        OrdenServicioCategoria dto = new OrdenServicioCategoria();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setActivo(entity.getActivo());

        return dto;
    }

    public OrdenesServiciosCategoriaEntity updateCategoria(OrdenServicioCategoria ordenServicioCategoria, OrdenesServiciosCategoriaEntity entity) {
        entity.setNombre(ordenServicioCategoria.getNombre());
        entity.setActivo(ordenServicioCategoria.getActivo());

        return entity;
    }

}
