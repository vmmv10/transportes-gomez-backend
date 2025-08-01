package com.transporte_gomez.erp.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class Documento {

    private Long id;
    private Usuario usuario;
    private String numero;
    private Proveedor proveedor;
    private Escuela escuela;
    private List<MultipartFile> files;
    private DocumentoTipo tipo;
    private Bodega bodega;
    private boolean entregado;
    private boolean asignado;
}
