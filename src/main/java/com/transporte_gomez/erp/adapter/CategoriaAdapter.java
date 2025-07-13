package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Categoria;
import com.transporte_gomez.erp.entity.CategoriaEntity;
import org.springframework.stereotype.Service;

@Service
public class CategoriaAdapter {

    public Categoria get(CategoriaEntity categoriaEntity) {
        if (categoriaEntity == null) {
            return null;
        }
        Categoria categoria = new Categoria();
        categoria.setId(categoriaEntity.getId());
        categoria.setNombre(categoriaEntity.getNombre());
        categoria.setDescripcion(categoriaEntity.getDescripcion());
        return categoria;
    }
}
