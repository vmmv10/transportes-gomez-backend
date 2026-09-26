package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Ingresos;
import com.transporte_gomez.erp.entity.IngresosEntity;
import com.transporte_gomez.erp.enums.IngresoEstado;
import com.transporte_gomez.erp.repository.BodegaRepository;
import com.transporte_gomez.erp.repository.ClienteRepository;
import com.transporte_gomez.erp.repository.DocumentoTipoRepository;
import com.transporte_gomez.erp.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IngresosAdapter {

    private final DocumentoTipoAdapter documentoTipoAdapter;
    private final IngresosDetalleAdapter ingresoEmergenciaDetalleAdapter;
    private final DocumentoTipoRepository documentoTipoRepository;
    private final BodegaRepository bodegaRepository;
    private final BodegaAdapter bodegaAdapter;
    private final ClienteAdapter clienteAdapter;
    private final ProveedorAdpater proveedorAdpater;
    private final BultoAdapter bultoAdapter;
    private final ClienteRepository clienteRepository;
    private final ProveedorRepository proveedorRepository;

    public Ingresos toDto(IngresosEntity entity, Boolean conDetalles) {
        if (entity == null) {
            return null;
        }

        Ingresos dto = new Ingresos();
        dto.setId(entity.getId());
        dto.setFecha(String.valueOf(entity.getFecha()));
        dto.setFechaCierre(entity.getFechaCierre() != null ? String.valueOf(entity.getFechaCierre()): null);
        dto.setDocumento(entity.getDocumento());
        if (entity.getDocumentoTipo() != null) {
            dto.setDocumentoTipo(documentoTipoAdapter.getDocumentoTipo(entity.getDocumentoTipo()));
        }
        dto.setObservaciones(entity.getObservaciones());
        dto.setBodega(bodegaAdapter.getBodega(entity.getBodega()));
        dto.setEstado(entity.getEstado());
        dto.setOrdenCompra(entity.getOrdenCompra());
        dto.setCliente(clienteAdapter.toDtoResumen(entity.getCliente()));
        if (entity.getTransportista() != null) {
            dto.setTransportista(proveedorAdpater.getProveedor(entity.getTransportista()));
        }
        dto.setGuiaTransportista(entity.getGuiaTransportista());

        if (conDetalles) {
            dto.setDetalles(entity.getDetalles().stream()
                    .map(ingresoEmergenciaDetalleAdapter::get)
                    .toList());
            dto.setBultos(entity.getBultos().stream()
                    .map(bultoAdapter::toDto)
                    .toList());
        } else {
            dto.setDetalles(null);
        }

        return dto;
    }

    public IngresosEntity toEntity(Ingresos dto) {
        if (dto == null) {
            return null;
        }

        IngresosEntity entity = new IngresosEntity();

        if (dto.getDocumentoTipo() != null) {
            entity.setDocumentoTipo(documentoTipoRepository.findById(dto.getDocumentoTipo().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Documento Tipo no encontrado con ID: " + dto.getDocumentoTipo().getId())));
        }
        entity.setDocumento(dto.getDocumento());
        entity.setObservaciones(dto.getObservaciones());
        entity.setBodega(bodegaRepository.findById(dto.getBodega().getId())
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada con ID: " + dto.getBodega().getId())));
        entity.setEstado(IngresoEstado.TERMPORAL.getCodigo());
        entity.setOrdenCompra(dto.getOrdenCompra());
        aplicarRoles(dto, entity);
        return entity;
    }

    public IngresosEntity updateEntity(Ingresos dto, IngresosEntity entity) {
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
        aplicarRoles(dto, entity);

        return entity;
    }

    /**
     * Cliente (quién paga), transportista (quién trae la carga) y N° de guía.
     * Solo cambia lo que viene en la solicitud, para no borrar datos
     * cuando una pantalla antigua no los envía.
     */
    private void aplicarRoles(Ingresos dto, IngresosEntity entity) {
        if (dto.getCliente() != null && dto.getCliente().getId() != null) {
            entity.setCliente(clienteRepository.findById(dto.getCliente().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + dto.getCliente().getId())));
        }
        if (dto.getTransportista() != null && dto.getTransportista().getId() != null) {
            entity.setTransportista(proveedorRepository.findById(dto.getTransportista().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Transportista no encontrado con ID: " + dto.getTransportista().getId())));
        }
        if (dto.getGuiaTransportista() != null) {
            entity.setGuiaTransportista(dto.getGuiaTransportista().isBlank() ? null : dto.getGuiaTransportista().trim());
        }
    }

}
