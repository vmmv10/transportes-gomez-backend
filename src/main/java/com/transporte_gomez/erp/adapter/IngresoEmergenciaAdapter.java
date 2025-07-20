package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.IngresosEmergencia;
import com.transporte_gomez.erp.entity.IngresosEmergenciaEntity;
import com.transporte_gomez.erp.enums.IngresoEmergenciaEstado;
import com.transporte_gomez.erp.repository.BodegaRepository;
import com.transporte_gomez.erp.repository.DocumentoTipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngresoEmergenciaAdapter {

    private final DocumentoTipoAdapter documentoTipoAdapter;
    private final IngresoEmergenciaDetalleAdapter ingresoEmergenciaDetalleAdapter;
    private final DocumentoTipoRepository documentoTipoRepository;
    private final BodegaRepository bodegaRepository;
    private final BodegaAdapter bodegaAdapter;

    public IngresosEmergencia toDto(IngresosEmergenciaEntity entity, Boolean conDetalles) {
        if (entity == null) {
            return null;
        }

        IngresosEmergencia dto = new IngresosEmergencia();
        dto.setId(entity.getId());
        dto.setFecha(String.valueOf(entity.getFecha()));
        dto.setDocumento(entity.getDocumento());
        dto.setDocumentoTipo(documentoTipoAdapter.getDocumentoTipo(entity.getDocumentoTipo()));
        dto.setObservaciones(entity.getObservaciones());
        dto.setBodega(bodegaAdapter.getBodega(entity.getBodega()));
        dto.setEstado(entity.getEstado());

        if (conDetalles) {
            dto.setDetalles(entity.getDetalles().stream()
                    .map(ingresoEmergenciaDetalleAdapter::get)
                    .toList());
        } else {
            dto.setDetalles(null);
        }

        return dto;
    }

    public IngresosEmergenciaEntity toEntity(IngresosEmergencia dto) {
        if (dto == null) {
            return null;
        }

        IngresosEmergenciaEntity entity = new IngresosEmergenciaEntity();

        if (dto.getDocumentoTipo() != null) {
            entity.setDocumentoTipo(documentoTipoRepository.findById(dto.getDocumentoTipo().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Documento Tipo no encontrado con ID: " + dto.getDocumentoTipo().getId())));
        }
        entity.setDocumento(dto.getDocumento());
        entity.setObservaciones(dto.getObservaciones());
        entity.setBodega(bodegaRepository.findById(3L)
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada con ID: " + dto.getBodega().getId())));
        entity.setEstado(IngresoEmergenciaEstado.TERMPORAL.getCodigo());
        return entity;
    }

    public IngresosEmergenciaEntity updateEntity(IngresosEmergencia dto, IngresosEmergenciaEntity entity) {
        if (dto == null || entity == null) {
            return entity;
        }

        if (dto.getDocumentoTipo() != null) {
            entity.setDocumentoTipo(documentoTipoRepository.findById(dto.getDocumentoTipo().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Documento Tipo no encontrado con ID: " + dto.getDocumentoTipo().getId())));
        }
        entity.setDocumento(dto.getDocumento());
        entity.setObservaciones(dto.getObservaciones());
        if (dto.getBodega() != null) {
            entity.setBodega(bodegaRepository.findById(dto.getBodega().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada con ID: " + dto.getBodega().getId())));
        }

        return entity;
    }

}
