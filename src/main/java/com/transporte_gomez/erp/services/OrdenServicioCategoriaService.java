package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.OrdenServicioCategoriaAdapter;
import com.transporte_gomez.erp.dto.OrdenServicioCategoria;
import com.transporte_gomez.erp.entity.OrdenesServiciosCategoriaEntity;
import com.transporte_gomez.erp.repository.OrdenesServiciosCategoriaRepository;
import com.transporte_gomez.erp.specification.OrdenServicioCategoriaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenServicioCategoriaService {

    private final OrdenesServiciosCategoriaRepository repository;
    private final OrdenServicioCategoriaAdapter adapter;

    public Page<OrdenServicioCategoria> findAll(Pageable pageable, OrdenServicioCategoria filtro) {
        return repository.findAll(OrdenServicioCategoriaSpecification.conFiltros(filtro), pageable)
                .map(adapter::toDTO);
    }

    public OrdenServicioCategoria createCategoria(OrdenServicioCategoria ordenServicioCategoria) {
        OrdenesServiciosCategoriaEntity entity = adapter.createCategoria(ordenServicioCategoria);
        OrdenesServiciosCategoriaEntity savedEntity = repository.save(entity);
        return adapter.toDTO(savedEntity);
    }

    public OrdenServicioCategoria updateCategoria(Integer id, OrdenServicioCategoria ordenServicioCategoria) {
        OrdenesServiciosCategoriaEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        OrdenesServiciosCategoriaEntity updatedEntity = adapter.updateCategoria(ordenServicioCategoria, entity);
        OrdenesServiciosCategoriaEntity savedEntity = repository.save(updatedEntity);
        return adapter.toDTO(savedEntity);
    }

    public void desactivarCategoria(Integer id) {
        OrdenesServiciosCategoriaEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        entity.setActivo(false);
        repository.save(entity);
    }

    public void activarCategoria(Integer id) {
        OrdenesServiciosCategoriaEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        entity.setActivo(true);
        repository.save(entity);
    }

    public OrdenServicioCategoria findById(Integer id) {
        OrdenesServiciosCategoriaEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        return adapter.toDTO(entity);
    }

    public List<OrdenServicioCategoria> findAll() {
        return repository.findByActivo(true).stream()
                .map(adapter::toDTO)
                .toList();
    }
}
