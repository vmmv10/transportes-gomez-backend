package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Documento;
import com.transporte_gomez.erp.dto.DocumentoFiltro;
import com.transporte_gomez.erp.entity.DocumentoEntity;
import com.transporte_gomez.erp.services.DocumentoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;

    @GetMapping()
    public Page<Documento> getDocumentos(Pageable pageable, DocumentoFiltro documentoFiltro) {
        return documentoService.findAll(pageable, documentoFiltro);
    }

    @GetMapping("/{id}")
    public Documento getDocumentoById(@PathVariable Long id) {
        return documentoService.getById(id);
    }

    @PostMapping
    public Documento createDocumento(@RequestBody Documento documento) {
        return documentoService.createDocumento(documento);
    }

    @PutMapping("/{id}")
    public Documento updateDocumento(@PathVariable Long id, @RequestBody Documento documento) {
        return documentoService.updateDocumento(id, documento);
    }

    @DeleteMapping("/{id}")
    public void deleteDocumento(@PathVariable Long id) {
        documentoService.deleteDocumento(id);
    }
}
