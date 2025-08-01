package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Marca;
import com.transporte_gomez.erp.entity.MarcaEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;

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

    public MarcaEntity create(Marca marca) {
        if (marca == null) {
            return null;
        }
        MarcaEntity marcaEntity = new MarcaEntity();
        marcaEntity.setNombre(marca.getNombre());
        marcaEntity.setDescripcion(marca.getDescripcion());
        marcaEntity.setActivo(true);
        marcaEntity.setFechaCreacion(Instant.now());
        return marcaEntity;
    }

    public MarcaEntity update(MarcaEntity updatedMarcaEntity, Marca marca) {
        updatedMarcaEntity.setNombre(marca.getNombre());
        updatedMarcaEntity.setDescripcion(marca.getDescripcion());
        return updatedMarcaEntity;
    }
}
