package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.DocumentoAdapter;
import com.transporte_gomez.erp.dto.Documento;
import com.transporte_gomez.erp.dto.DocumentoFiltro;
import com.transporte_gomez.erp.entity.DocumentoEntity;
import com.transporte_gomez.erp.enums.Modulo;
import com.transporte_gomez.erp.repository.DocumentoRepository;
import com.transporte_gomez.erp.specification.DocumentoSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentoService {

    @Value("${ruta.documentos}")
    private String rutaDocumentos;

    private final DocumentoRepository documentoRepository;
    private final DocumentoAdapter documentoAdapter;
    private final ImagenService imagenService;

    public Page<Documento> findAll(Pageable pageable, DocumentoFiltro filtro) {
        return documentoRepository.findAll(DocumentoSpecification.conFiltros(filtro), pageable)
                .map(documentoAdapter::getDocumento);
    }

    public Documento getById(Long id) {
        return documentoRepository.findById(id)
                .map(documentoAdapter::getDocumento)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado con ID: " + id));
    }

    public Documento getByNumero(String id, Integer tipo) {
        DocumentoEntity documentoEntity = documentoRepository.findByNumeroAndTipo_Codigo(id, tipo);
        if (documentoEntity == null) {
            throw new IllegalArgumentException("Documento no encontrado con ID: " + id + " y tipo: " + tipo);
        }
        return documentoAdapter.getDocumento(documentoEntity);
    }

    public Documento createDocumento(Documento documento, List<MultipartFile> files) {
        DocumentoEntity documentoEntity = documentoAdapter.createEntity(documento);
        DocumentoEntity documentoEntitySave = documentoRepository.save(documentoEntity);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    imagenService.guardarArchivo(file, Modulo.DOCUMENTO.getCodigo(), documentoEntitySave.getId(), rutaDocumentos);
                } catch (Exception e) {
                    throw new RuntimeException("Error al guardar la imagen: " + e.getMessage(), e);
                }
            }
        }

        return documentoAdapter.getDocumento(documentoEntitySave);
    }

    public Documento updateDocumento(Long id, Documento documento, List<MultipartFile> files) {
        DocumentoEntity documentoEntity = documentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado con ID: " + id));
        log.info("Actualizando documento con ID: {}", id);
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    imagenService.guardarArchivo(file, Modulo.DOCUMENTO.getCodigo(), documentoEntity.getId(), rutaDocumentos);
                } catch (Exception e) {
                    throw new RuntimeException("Error al guardar la imagen: " + e.getMessage(), e);
                }
            }
        }
        documentoEntity = documentoAdapter.updateEntity(documentoEntity, documento);
        documentoEntity = documentoRepository.save(documentoEntity);
        return documentoAdapter.getDocumento(documentoEntity);
    }

    public void deleteDocumento(Long id) {
        documentoRepository.deleteById(id);
        imagenService.eliminarImagenByEntidad(Modulo.DOCUMENTO.getCodigo(), id);
    }

    public void asignarDocumento(Long id, boolean asignado) {
        DocumentoEntity documentoEntity = documentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado con ID: " + id));
        documentoEntity.setAsignado(asignado);
        documentoRepository.save(documentoEntity);
    }
}
