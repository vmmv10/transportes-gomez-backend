package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.ComunaAdapter;
import com.transporte_gomez.erp.dto.Comuna;
import com.transporte_gomez.erp.repository.ComunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComunaService {

    private final ComunaRepository comunaRepository;
    private final ComunaAdapter comunaAdapter;

    public List<Comuna> listar() {
        return comunaRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(comunaAdapter::toDto)
                .toList();
    }
}
