package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.DocumentoAdapter;
import com.transporte_gomez.erp.dto.Documento;
import com.transporte_gomez.erp.dto.DocumentoFiltro;
import com.transporte_gomez.erp.entity.DocumentoEntity;
import com.transporte_gomez.erp.repository.DocumentoRepository;
import com.transporte_gomez.erp.specification.DocumentoSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final DocumentoAdapter documentoAdapter;

    public Page<Documento> findAll(Pageable pageable, DocumentoFiltro filtro) {
        return documentoRepository.findAll(DocumentoSpecification.conFiltros(filtro), pageable)
                .map(documentoAdapter::getDocumento);
    }

    public Documento getById(Long id) {
        return documentoRepository.findById(id)
                .map(documentoAdapter::getDocumento)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado con ID: " + id));
    }

    public Documento createDocumento(Documento documento) {
        DocumentoEntity documentoEntity = documentoAdapter.createEntity(documento);
        documentoEntity = documentoRepository.save(documentoEntity);
        return documentoAdapter.getDocumento(documentoEntity);
    }

    public Documento updateDocumento(Long id, Documento documento) {
        DocumentoEntity documentoEntity = documentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado con ID: " + id));
        documentoEntity = documentoAdapter.updateEntity(documentoEntity);
        documentoEntity = documentoRepository.save(documentoEntity);
        return documentoAdapter.getDocumento(documentoEntity);
    }

    public void deleteDocumento(Long id) {
        documentoRepository.deleteById(id);
    }
}
