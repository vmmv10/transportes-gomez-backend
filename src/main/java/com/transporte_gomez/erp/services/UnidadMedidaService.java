package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.UnidadMedidaAdapter;
import com.transporte_gomez.erp.dto.UnidadMedida;
import com.transporte_gomez.erp.entity.UnidadesMedidaEntity;
import com.transporte_gomez.erp.repository.UnidadesMedidaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UnidadMedidaService {

    private final UnidadesMedidaRepository unidadMedidaRepository;
    private final UnidadMedidaAdapter unidadMedidaAdapter;

    public List<UnidadMedida> findAll() {
        List<UnidadesMedidaEntity> unidadMedidaEntities = unidadMedidaRepository.findAll();
        return unidadMedidaAdapter.getUnidadesMedida(unidadMedidaEntities);
    }

}
