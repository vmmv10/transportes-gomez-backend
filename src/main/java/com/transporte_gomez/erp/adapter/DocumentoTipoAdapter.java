package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.DocumentoTipo;
import com.transporte_gomez.erp.entity.DocumentoTipoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentoTipoAdapter {

    public DocumentoTipo getDocumentoTipo(DocumentoTipoEntity documentoTipoEntity) {
        DocumentoTipo documentoTipo = new DocumentoTipo();
        documentoTipo.setId(documentoTipoEntity.getId());
        documentoTipo.setNombre(documentoTipoEntity.getNombre());
        documentoTipo.setCodigo(documentoTipoEntity.getCodigo());
        return documentoTipo;
    }

}
