package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.IngresoConversacion;
import com.transporte_gomez.erp.entity.IngresoConversacionEntity;
import com.transporte_gomez.erp.entity.IngresosEntity;
import com.transporte_gomez.erp.repository.IngresoConversacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IngresosConversacionesAdapter {

    public IngresoConversacionEntity create(IngresosEntity ingresosEntity) {
        IngresoConversacionEntity conversacionEntity = new IngresoConversacionEntity();
        conversacionEntity.setIngreso(ingresosEntity);
        conversacionEntity.setCerrada(Boolean.FALSE);
        conversacionEntity.setCreadaEn(LocalDateTime.now());

        return conversacionEntity;
    }

    public IngresoConversacion get(IngresoConversacionEntity conversacionEntity) {

        IngresoConversacion conversacion = new IngresoConversacion();
        conversacion.setId(conversacionEntity.getId());
        conversacion.setIngreso(conversacionEntity.getIngreso().getId());
        conversacion.setCerrado(conversacionEntity.getCerrada());

        return conversacion;
    }
}
