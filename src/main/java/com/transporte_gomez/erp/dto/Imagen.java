package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class Imagen {

    private Long id;
    private String entidadTipo;
    private Long entidadId;
    private String nombreOriginal;
    private String ruta;

    private String itemImageSrc;
    private String thumbnailImageSrc;
    private String alt;
    private String title;
}
