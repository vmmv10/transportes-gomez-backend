package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Imagen;
import com.transporte_gomez.erp.entity.ImagenEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImagenAdapter {

    @Value("${api}")
    private String apiUrl;

    public Imagen getImagen(ImagenEntity imagenEntity) {
        Imagen imagen = new Imagen();
        imagen.setId(imagenEntity.getId());
        imagen.setEntidadTipo(imagenEntity.getEntidadTipo());
        imagen.setEntidadId(imagenEntity.getEntidadId());
        imagen.setNombreOriginal(imagenEntity.getNombreOriginal());
        imagen.setRuta(imagenEntity.getRuta());

        imagen.setItemImageSrc(apiUrl + imagenEntity.getRuta());
        imagen.setThumbnailImageSrc(apiUrl + imagenEntity.getRuta());
        imagen.setAlt(imagenEntity.getNombreOriginal());
        imagen.setTitle(imagenEntity.getNombreOriginal());

        return imagen;
    }
}
