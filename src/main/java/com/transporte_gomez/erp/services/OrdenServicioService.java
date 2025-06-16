package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.OrdenServicioAdapter;
import com.transporte_gomez.erp.adapter.OrdenServicioDetalleAdapter;
import com.transporte_gomez.erp.dto.OrdenServicio;
import com.transporte_gomez.erp.dto.OrdenServicioFiltro;
import com.transporte_gomez.erp.dto.Usuario;
import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import com.transporte_gomez.erp.enums.AuditoriaOperacion;
import com.transporte_gomez.erp.enums.Modulo;
import com.transporte_gomez.erp.repository.OrdenServicioRepository;
import com.transporte_gomez.erp.specification.OrdenServicioSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RequiredArgsConstructor
@Service
public class OrdenServicioService {

    @Value("${ruta.documentos}")
    private String rutaOrdenes;

    private final OrdenServicioDetalleService ordenServicioDetalleService;
    private final OrdenServicioRepository ordenServicioRepository;
    private final OrdenServicioAdapter ordenServicioAdapter;
    private final AuditoriaService auditoriaService;
    private final ImagenService imagenService;

    public Page<OrdenServicio> getOrdenServicios(Pageable pageable, OrdenServicioFiltro filtro){
        return ordenServicioRepository.findAll(OrdenServicioSpecification.conFiltros(filtro), pageable)
                .map(entity -> ordenServicioAdapter.getOrdenServicio(entity, false));
    }

    public OrdenServicio getOrdenServicio(Long id) {
        return ordenServicioRepository.findById(id)
                .map(entity -> ordenServicioAdapter.getOrdenServicio(entity, true))
                .orElse(null);
    }

    public OrdenServicio createOrdenServicio(OrdenServicio ordenServicio, Usuario usuario) {
        OrdenServicioEntity ordenServicioEntitySave = ordenServicioRepository.save(ordenServicioAdapter.createOrdenServicio(ordenServicio));
        if (ordenServicio.getDetalles() != null && !ordenServicio.getDetalles().isEmpty()) {
            ordenServicioDetalleService.create(ordenServicio.getDetalles(), ordenServicioEntitySave);
        }
        auditoriaService.registrarAuditoria(AuditoriaOperacion.CREADO.getNombre(), Modulo.ORDEN_SERVICIO.getCodigo(), ordenServicioEntitySave.getId(), usuario.getId());
        return ordenServicioAdapter.getOrdenServicio(ordenServicioEntitySave, true);
    }

    public OrdenServicio updateOrdenServicio(Long id, OrdenServicio ordenServicio, List<MultipartFile> files, Usuario usuario) {
        OrdenServicioEntity ordenServicioEntity = ordenServicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden de servicio no encontrada con ID: " + id));

        OrdenServicioEntity updatedEntity = ordenServicioRepository.save(ordenServicioAdapter.updateOrdenServicio(ordenServicioEntity, ordenServicio));
        if (ordenServicio.getDetalles() != null && !ordenServicio.getDetalles().isEmpty()) {
            ordenServicioDetalleService.update(ordenServicio.getDetalles(), updatedEntity);
        }
        auditoriaService.registrarAuditoria(AuditoriaOperacion.ACTUALIZADO.getNombre(), Modulo.ORDEN_SERVICIO.getCodigo(), updatedEntity.getId(), usuario.getId());

        return ordenServicioAdapter.getOrdenServicio(updatedEntity, true);
    }

    public void deleteOrdenServicio(Long id) {
        ordenServicioRepository.deleteById(id);
    }

    public void entregado(Long id) {
        OrdenServicioEntity ordenServicio = ordenServicioRepository.findById(id)
                .orElse(null);
        if (ordenServicio != null) {
            ordenServicio.setEntregado(true);
            ordenServicioRepository.save(ordenServicio);
        } else {
            throw new IllegalArgumentException("Orden de servicio no encontrada con ID: " + id);
        }
    }

}
