package com.transporte_gomez.erp.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class Documento {

    private Long id;
    private Usuario usuario;
    private Long numero;
    private Proveedor proveedor;
    private Escuela escuela;
    private List<MultipartFile> files;
    private DocumentoTipo tipo;
}
