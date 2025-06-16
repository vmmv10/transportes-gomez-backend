package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.OrdenServicioDetalleAdapter;
import com.transporte_gomez.erp.dto.OrdenServicioDetalle;
import com.transporte_gomez.erp.entity.OrdenServicioDetalleEntity;
import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import com.transporte_gomez.erp.repository.OrdenServicioDetalleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrdenServicioDetalleService {

    private final OrdenServicioDetalleRepository ordenServicioDetalleRepository;
    private final OrdenServicioDetalleAdapter ordenServicioDetalleAdapter;

    public void create(List<OrdenServicioDetalle> ordenServicioDetalleList, OrdenServicioEntity ordenServicioEntitySave) {
        List<OrdenServicioDetalleEntity> ordenServicioDetalleEntitySave = new ArrayList<>();
        for (OrdenServicioDetalle detalle : ordenServicioDetalleList) {
            OrdenServicioDetalleEntity detalleEntity = ordenServicioDetalleAdapter.createOrdenServicioDetalle(detalle);
            detalleEntity.setOrdenServicio(ordenServicioEntitySave);
            ordenServicioDetalleEntitySave.add(detalleEntity);
        }
        ordenServicioDetalleRepository.saveAll(ordenServicioDetalleEntitySave);
    }

    public void update(List<OrdenServicioDetalle> ordenServicioDetalleList, OrdenServicioEntity ordenServicioEntity) {
        List<OrdenServicioDetalleEntity> ordenServicioDetalleEntitySave = new ArrayList<>();
        for (OrdenServicioDetalle detalle : ordenServicioDetalleList) {
            OrdenServicioDetalleEntity detalleEntity;
            if (detalle.getId() == null || detalle.getId() <= 0) {
                detalleEntity = ordenServicioDetalleAdapter.createOrdenServicioDetalle(detalle);
            } else {
                log.info("----------------------Actualizando detalle con ID: {}", detalle.getId());
                detalleEntity = ordenServicioDetalleRepository.findById(detalle.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado con ID: " + detalle.getId()));
                detalleEntity = ordenServicioDetalleAdapter.updateOrdenServicioDetalle(detalle, detalleEntity);
            }
            detalleEntity.setOrdenServicio(ordenServicioEntity);
            ordenServicioDetalleEntitySave.add(detalleEntity);
        }
        ordenServicioDetalleRepository.saveAll(ordenServicioDetalleEntitySave);
    }

    public void delete(Long id) {
        ordenServicioDetalleRepository.deleteById(id);
    }
}
