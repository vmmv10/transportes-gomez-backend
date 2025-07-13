package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.MarcaAdapter;
import com.transporte_gomez.erp.dto.Marca;
import com.transporte_gomez.erp.repository.MarcaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaAdapter marcaAdapter;
    private final MarcaRepository marcaRepository;

    public List<Marca> getAll() {
        return marcaRepository.findAll().stream()
                .map(marcaAdapter::get)
                .toList();
    }
}
