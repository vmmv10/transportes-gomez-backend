package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Comuna;
import com.transporte_gomez.erp.entity.ComunaEntity;
import org.springframework.stereotype.Service;

@Service
public class ComunaAdapter {

    public Comuna toDto(ComunaEntity entity) {
        if (entity == null) {
            return null;
        }
        Comuna dto = new Comuna();
        dto.setId(entity.getId());
        dto.setCodigoIne(entity.getCodigoIne());
        dto.setNombre(entity.getNombre());
        dto.setProvincia(entity.getProvincia());
        dto.setRegion(entity.getRegion());
        dto.setActivo(entity.getActivo());
        return dto;
    }
}
