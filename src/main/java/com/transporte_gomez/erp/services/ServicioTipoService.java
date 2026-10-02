package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.ServicioTipoAdapter;
import com.transporte_gomez.erp.dto.ServicioTipo;
import com.transporte_gomez.erp.repository.ServicioTipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioTipoService {

    private final ServicioTipoRepository servicioTipoRepository;
    private final ServicioTipoAdapter servicioTipoAdapter;

    public List<ServicioTipo> listar() {
        return servicioTipoRepository.findByActivoTrueOrderByIdAsc().stream()
                .map(servicioTipoAdapter::toDto)
                .toList();
    }
}
