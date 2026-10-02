package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.ServicioTipo;
import com.transporte_gomez.erp.entity.ServicioTipoEntity;
import org.springframework.stereotype.Service;

@Service
public class ServicioTipoAdapter {

    public ServicioTipo toDto(ServicioTipoEntity entity) {
        if (entity == null) {
            return null;
        }
        ServicioTipo dto = new ServicioTipo();
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setNombre(entity.getNombre());
        dto.setCategoria(entity.getCategoria());
        dto.setModalidad(entity.getModalidad());
        dto.setActivo(entity.getActivo());
        return dto;
    }
}
