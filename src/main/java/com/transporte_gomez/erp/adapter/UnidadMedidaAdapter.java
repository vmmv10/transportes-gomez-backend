package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.UnidadMedida;
import com.transporte_gomez.erp.entity.UnidadesMedidaEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnidadMedidaAdapter {

    public UnidadMedida getUnidadMedida(UnidadesMedidaEntity unidadMedidaEntity) {
        UnidadMedida unidad = new UnidadMedida();
        unidad.setNombre(unidadMedidaEntity.getNombre());
        unidad.setId(unidadMedidaEntity.getId());
        unidad.setSimbolo(unidadMedidaEntity.getSimbolo());
        unidad.setCodigo(unidadMedidaEntity.getCodigo());

        return unidad;
    }

    public List<UnidadMedida> getUnidadesMedida(List<UnidadesMedidaEntity> unidadesMedidaEntityList) {
        return unidadesMedidaEntityList.stream()
                .map(this::getUnidadMedida)
                .toList();
    }
}
