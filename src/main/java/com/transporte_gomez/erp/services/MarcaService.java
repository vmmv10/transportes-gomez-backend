package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.MarcaAdapter;
import com.transporte_gomez.erp.dto.Marca;
import com.transporte_gomez.erp.dto.MarcaFiltro;
import com.transporte_gomez.erp.entity.MarcaEntity;
import com.transporte_gomez.erp.repository.MarcaRepository;
import com.transporte_gomez.erp.specification.MarcaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaAdapter marcaAdapter;
    private final MarcaRepository marcaRepository;

    public List<Marca> getList() {
        return marcaRepository.findAll().stream()
                .map(marcaAdapter::get)
                .toList();
    }

    public Page<Marca> findAll(MarcaFiltro filtro, Pageable pageable) {
        Page<MarcaEntity> page = marcaRepository.findAll(MarcaSpecification.conFiltros(filtro), pageable);
        return page.map(marcaAdapter::get);
    }

    public Marca getById(Integer id) {
        MarcaEntity marcaEntity = marcaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada con ID: " + id));
        return marcaAdapter.get(marcaEntity);
    }

    public Marca create(Marca marca) {
        MarcaEntity marcaEntity = marcaAdapter.create(marca);
        marcaEntity = marcaRepository.save(marcaEntity);
        return marcaAdapter.get(marcaEntity);
    }

    public Marca update(Integer id, Marca marca) {
        MarcaEntity marcaEntity = marcaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada con ID: " + id));
        marcaEntity = marcaAdapter.update(marcaEntity, marca);
        marcaEntity = marcaRepository.save(marcaEntity);
        return marcaAdapter.get(marcaEntity);
    }

    public void desactive(Integer id) {
        MarcaEntity marcaEntity = marcaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada con ID: " + id));
        marcaEntity.setActivo(false);
        marcaRepository.save(marcaEntity);
    }
}
