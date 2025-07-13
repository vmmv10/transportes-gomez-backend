package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Marca;
import com.transporte_gomez.erp.entity.MarcaEntity;
import org.springframework.stereotype.Service;

@Service
public class MarcaAdapter {

    public Marca get(MarcaEntity marcaEntity) {
        if (marcaEntity == null) {
            return null;
        }
        Marca marca = new Marca();
        marca.setId(marcaEntity.getId());
        marca.setNombre(marcaEntity.getNombre());
        marca.setDescripcion(marcaEntity.getDescripcion());
        return marca;
    }
}
