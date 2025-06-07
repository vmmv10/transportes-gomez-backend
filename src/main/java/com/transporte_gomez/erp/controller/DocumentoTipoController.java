package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.DocumentoTipo;
import com.transporte_gomez.erp.services.DocumentoTipoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/documentos-tipos")
public class DocumentoTipoController {

    public final DocumentoTipoService documentoTipoService;

    @GetMapping()
    public List<DocumentoTipo> getDocumentoTipos() {
        return documentoTipoService.findAll();
    }
}
