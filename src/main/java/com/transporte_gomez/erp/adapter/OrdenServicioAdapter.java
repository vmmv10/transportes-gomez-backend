package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.OrdenServicio;
import com.transporte_gomez.erp.dto.OrdenServicioDetalle;
import com.transporte_gomez.erp.entity.OrdenServicioDetalleEntity;
import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import com.transporte_gomez.erp.repository.DocumentoRepository;
import com.transporte_gomez.erp.repository.EscuelaRepository;
import com.transporte_gomez.erp.repository.OrdenServicioDetalleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdenServicioAdapter {

    private final OrdenServicioDetalleAdapter ordenServicioDetalleAdapter;
    private final ProveedorAdpater proveedorAdpater;
    private final DocumentoAdapter documentoAdapter;
    private final EscuelaAdapter escuelaAdapter;
    private final OrdenServicioDetalleRepository ordenServicioDetalleRepository;
    private final EscuelaRepository escuelaRepository;
    private final DocumentoRepository documentoRepository;

    public OrdenServicio getOrdenServicio(OrdenServicioEntity ordenServicioEntity, boolean conDetalles) {
        OrdenServicio ordenServicio = new OrdenServicio();

        ordenServicio.setId(ordenServicioEntity.getId());
        ordenServicio.setDocumento(documentoAdapter.getDocumento(ordenServicioEntity.getDocumento()));
        ordenServicio.setFecha(ordenServicioEntity.getFecha().toOffsetDateTime());
        ordenServicio.setEscuela(escuelaAdapter.toDto(ordenServicioEntity.getEscuela()));
        ordenServicio.setEntregado(ordenServicioEntity.getEntregado());

        if (conDetalles && ordenServicioEntity.getDetalles() != null) {
            List<OrdenServicioDetalle> detalles = new ArrayList<>();

            ordenServicioEntity.getDetalles().forEach(detalleEntity -> {
                OrdenServicioDetalle detalle = ordenServicioDetalleAdapter.getOrdenServicioDetalle(detalleEntity);
                detalles.add(detalle);
            });

            ordenServicio.setDetalles(detalles);
        }

        return ordenServicio;
    }

    public OrdenServicioEntity createOrdenServicio(OrdenServicio ordenServicio) {
        OrdenServicioEntity ordenServicioEntity = new OrdenServicioEntity();
        ordenServicioEntity.setFecha(ZonedDateTime.now());
        ordenServicioEntity.setEntregado(false);
        ordenServicioEntity.setObservaciones(ordenServicio.getObservaciones());

        if (ordenServicio.getDocumento() != null) {
            ordenServicioEntity.setDocumento(documentoRepository.findByNumeroAndTipo_Codigo(ordenServicio.getDocumento().getNumero(), ordenServicio.getDocumento().getTipo().getCodigo()));
        }

        if (ordenServicio.getEscuela() != null) {
            ordenServicioEntity.setEscuela(escuelaRepository.findById(ordenServicio.getEscuela().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Escuela no encontrada con ID: " + ordenServicio.getEscuela().getId())));
        }

        return ordenServicioEntity;
    }

    public OrdenServicioEntity updateOrdenServicio(OrdenServicioEntity ordenServicioEntity, OrdenServicio ordenServicio) {
        ordenServicioEntity.setFecha(ZonedDateTime.now());
        ordenServicioEntity.setObservaciones(ordenServicio.getObservaciones());
        if (ordenServicio.getEscuela() != null) {
            ordenServicioEntity.setEscuela(escuelaRepository.findById(ordenServicio.getEscuela().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Escuela no encontrada con ID: " + ordenServicio.getEscuela().getId())));
        }

        if (ordenServicio.getDocumento() != null) {
            ordenServicioEntity.setDocumento(documentoRepository.findByNumeroAndTipo_Codigo(ordenServicio.getDocumento().getNumero(), ordenServicio.getDocumento().getTipo().getCodigo()));
        }

        return ordenServicioEntity;
    }



}
