package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.BodegaAdapter;
import com.transporte_gomez.erp.dto.Bodega;
import com.transporte_gomez.erp.repository.BodegaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BodegaService {

    private final BodegaRepository bodegaRepository;
    private final BodegaAdapter bodegaAdapter;

    public List<Bodega> getBodegas() {
        return bodegaRepository.findAll().stream()
                .map(bodegaAdapter::getBodega)
                .toList();
    }

    public Bodega getBodegaById(Long id) {
        return bodegaRepository.findById(id)
                .map(bodegaAdapter::getBodega)
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada con ID: " + id));
    }

    public Bodega createBodega(Bodega bodega) {
        var bodegaEntity = bodegaAdapter.createBodega(bodega);
        bodegaEntity = bodegaRepository.save(bodegaEntity);
        return bodegaAdapter.getBodega(bodegaEntity);
    }

    public Bodega updateBodega(Long id, Bodega bodega) {
        var bodegaEntity = bodegaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada con ID: " + id));
        bodegaEntity = bodegaAdapter.updateBodega(bodegaEntity, bodega);
        return bodegaAdapter.getBodega(bodegaRepository.save(bodegaEntity));
    }

    public void deleteBodega(Long id) {
        var bodegaEntity = bodegaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada con ID: " + id));
        bodegaRepository.delete(bodegaEntity);
    }

    public void desactivateBodega(Long id) {
        var bodegaEntity = bodegaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada con ID: " + id));
        bodegaEntity.setActivo(false);
        bodegaRepository.save(bodegaEntity);
    }
}
