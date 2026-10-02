package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.BultoAdapter;
import com.transporte_gomez.erp.dto.Bulto;
import com.transporte_gomez.erp.entity.BultoEntity;
import com.transporte_gomez.erp.entity.IngresosEntity;
import com.transporte_gomez.erp.repository.BultoRepository;
import com.transporte_gomez.erp.repository.IngresosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BultoService {

    private final BultoRepository bultoRepository;
    private final BultoAdapter bultoAdapter;
    private final IngresosRepository ingresosRepository;

    @Transactional(readOnly = true)
    public List<Bulto> listarPorIngreso(Integer ingresoId) {
        return bultoRepository.findByIngreso_IdOrderByIdAsc(ingresoId).stream()
                .map(bultoAdapter::toDto)
                .toList();
    }

    /** Búsqueda por N° de seguimiento externo (Starken, Kaiken...). */
    @Transactional(readOnly = true)
    public List<Bulto> buscarPorCodigoExterno(String codigoExterno) {
        return bultoRepository.findByCodigoExternoIgnoreCase(codigoExterno.trim()).stream()
                .map(bultoAdapter::toDto)
                .toList();
    }

    @Transactional
    public Bulto agregar(Integer ingresoId, Bulto bulto) {
        IngresosEntity ingreso = ingresosRepository.findById(ingresoId)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado con folio: " + ingresoId));
        BultoEntity entity = bultoAdapter.toEntity(bulto, new BultoEntity());
        entity.setIngreso(ingreso);
        entity.setEstado("EN_BODEGA");
        return bultoAdapter.toDto(bultoRepository.save(entity));
    }

    @Transactional
    public Bulto actualizar(Long id, Bulto bulto) {
        BultoEntity entity = buscar(id);
        if (!"EN_BODEGA".equals(entity.getEstado())) {
            throw new IllegalArgumentException("Solo se puede editar un bulto que está en bodega");
        }
        return bultoAdapter.toDto(bultoRepository.save(bultoAdapter.toEntity(bulto, entity)));
    }

    @Transactional
    public void eliminar(Long id) {
        BultoEntity entity = buscar(id);
        if (entity.getOrdenServicio() != null) {
            throw new IllegalArgumentException("El bulto ya está asignado a la orden " + entity.getOrdenServicio().getId());
        }
        bultoRepository.delete(entity);
    }

    private BultoEntity buscar(Long id) {
        return bultoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bulto no encontrado con ID: " + id));
    }
}
