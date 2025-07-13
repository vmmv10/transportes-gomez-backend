package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.DocumentoTipoAdapter;
import com.transporte_gomez.erp.dto.DocumentoTipo;
import com.transporte_gomez.erp.entity.DocumentoTipoEntity;
import com.transporte_gomez.erp.repository.DocumentoTipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DocumentoTipoService {

    private final DocumentoTipoRepository documentoTipoRepository;
    private final DocumentoTipoAdapter documentoTipoAdapter;

    public List<DocumentoTipo> findAll() {
        List<DocumentoTipoEntity> documentoTipoEntities = documentoTipoRepository.findAll();
        return documentoTipoEntities.stream()
                .map(documentoTipoAdapter::getDocumentoTipo)
                .toList();
    }

    public DocumentoTipo findById(Integer id) {
        DocumentoTipoEntity documentoTipoEntity = documentoTipoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento Tipo no encontrado con ID: " + id));
        return documentoTipoAdapter.getDocumentoTipo(documentoTipoEntity);
    }

    public DocumentoTipo findBySii(Integer siiCodigo) {
        DocumentoTipoEntity documentoTipoEntity = documentoTipoRepository.findByCodigo(siiCodigo);
        return documentoTipoAdapter.getDocumentoTipo(documentoTipoEntity);
    }
}
