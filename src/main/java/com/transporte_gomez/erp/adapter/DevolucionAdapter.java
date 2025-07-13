package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Devolucion;
import com.transporte_gomez.erp.dto.DevolucionDetalle;
import com.transporte_gomez.erp.entity.DevolucionDetalleEntity;
import com.transporte_gomez.erp.entity.DevolucionEntity;
import com.transporte_gomez.erp.enums.DevolucionEstado;
import com.transporte_gomez.erp.repository.EscuelaRepository;
import com.transporte_gomez.erp.repository.OrdenServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DevolucionAdapter {

    private final DevolucionDetalleAdapter devolucionDetalleAdapter;
    private final OrdenServicioRepository ordenServicioRepository;
    private final OrdenServicioAdapter ordenServicioAdapter;
    private final EscuelaRepository escuelaRepository;
    private final EscuelaAdapter escuelaAdapter;

    public Devolucion getDto(DevolucionEntity devolucionEntity, Boolean conDetalles) {
        Devolucion devolucion = new Devolucion();

        devolucion.setId(devolucionEntity.getId());
        devolucion.setFecha(String.valueOf(devolucionEntity.getFecha()));
        devolucion.setEstado(devolucionEntity.getEstado());
        devolucion.setEscuela(escuelaAdapter.toDto(devolucionEntity.getEscuela()));
        devolucion.setOrdenServicio(ordenServicioAdapter.getOrdenServicio(devolucionEntity.getOrden(),false));
        if (conDetalles) {
            devolucion.setDetalles(devolucionEntity.getDevolucionesDetalles()
                    .stream()
                    .map(devolucionDetalleAdapter::getDto)
                    .toList());
        }

        return  devolucion;
    }

    public DevolucionEntity createDevolucion(Devolucion devolucion) {
        DevolucionEntity devolucionEntity = new DevolucionEntity();
        devolucionEntity.setFecha(Instant.now());
        devolucionEntity.setEscuela(escuelaRepository.getReferenceById(devolucion.getEscuela().getId()));
        devolucionEntity.setOrden(ordenServicioRepository.getReferenceById(devolucion.getOrdenServicio().getId()));
        devolucionEntity.setEstado(DevolucionEstado.TERMPORAL.getCodigo());

        return devolucionEntity;
    }
}
