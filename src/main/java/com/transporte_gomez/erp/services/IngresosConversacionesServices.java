package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.IngresosConversacionesAdapter;
import com.transporte_gomez.erp.adapter.IngresosMensajeAdapter;
import com.transporte_gomez.erp.dto.IngresoConversacion;
import com.transporte_gomez.erp.dto.IngresoMensaje;
import com.transporte_gomez.erp.dto.Usuario;
import com.transporte_gomez.erp.entity.IngresoConversacionEntity;
import com.transporte_gomez.erp.entity.IngresoMensajeEntity;
import com.transporte_gomez.erp.entity.IngresosEntity;
import com.transporte_gomez.erp.repository.IngresoConversacionRepository;
import com.transporte_gomez.erp.repository.IngresoMensajeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngresosConversacionesServices {

    private final IngresosConversacionesAdapter ingresosConversacionesAdapter;
    private final IngresoConversacionRepository ingresoConversacionRepository;
    private final IngresoMensajeRepository ingresoMensajeRepository;
    private final IngresosMensajeAdapter ingresosMensajeAdapter;

    public void crearConversacion(IngresosEntity ingreso) {
        IngresoConversacionEntity conversacionEntity = ingresosConversacionesAdapter.create(ingreso);
        ingresoConversacionRepository.save(conversacionEntity);

    }

    public IngresoConversacion getConversacion(Integer id) {
        IngresoConversacionEntity conversacionEntity = ingresoConversacionRepository.findByIngreso_Id(id);
        IngresoConversacion conversacion = ingresosConversacionesAdapter.get(conversacionEntity);

        List<IngresoMensajeEntity> mensajesEntity = ingresoMensajeRepository.findByConversacion_IdOrderByFechaAsc(conversacionEntity.getId());

        if (!mensajesEntity.isEmpty()) {
            List<IngresoMensaje> mensajes = mensajesEntity.stream()
                    .map(ingresosMensajeAdapter::toDto)
                    .toList();
            conversacion.setMensajes(mensajes);
        }

        return conversacion;
    }

    public IngresoMensaje crearMensaje(Integer id, IngresoMensaje mensaje, Usuario usuario) {
        IngresoConversacionEntity conversacionEntity = ingresoConversacionRepository.findByIngreso_Id(id);

        if (conversacionEntity == null) {
            throw new IllegalArgumentException("No se encontró la conversación para el ingreso con ID: " + id);
        }

        IngresoMensajeEntity mensajeEntity = ingresosMensajeAdapter.crearMensaje(conversacionEntity, mensaje, usuario);

        IngresoMensajeEntity mensajeGuardado = ingresoMensajeRepository.save(mensajeEntity);

        return ingresosMensajeAdapter.toDto(mensajeGuardado);
    }
}
