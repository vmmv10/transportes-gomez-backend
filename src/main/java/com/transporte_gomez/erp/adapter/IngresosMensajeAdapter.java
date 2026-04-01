package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.IngresoMensaje;
import com.transporte_gomez.erp.dto.Usuario;
import com.transporte_gomez.erp.entity.IngresoConversacionEntity;
import com.transporte_gomez.erp.entity.IngresoMensajeEntity;
import com.transporte_gomez.erp.entity.UsuarioEntity;
import com.transporte_gomez.erp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IngresosMensajeAdapter {

    private final UsuarioRepository usuarioRepository;

    public IngresoMensajeEntity crearMensaje(IngresoConversacionEntity conversacionEntity, IngresoMensaje ingresosMensaje, Usuario usuario) {
        if (ingresosMensaje == null) {
            return null;
        }

        IngresoMensajeEntity mensaje = new IngresoMensajeEntity();
        mensaje.setMensaje(ingresosMensaje.getMensaje());

        if (usuario != null && usuario.getId() != null) {
            mensaje.setUsuario(usuarioRepository.findById(usuario.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + ingresosMensaje.getUsuario().getId())));
            mensaje.setEsSistema(Boolean.FALSE);
        } else {
            mensaje.setEsSistema(Boolean.TRUE);
        }

        mensaje.setFecha(LocalDateTime.now());
        mensaje.setConversacion(conversacionEntity);

        return mensaje;
    }

    public IngresoMensaje toDto(IngresoMensajeEntity entity) {
        if (entity == null) {
            return null;
        }

        IngresoMensaje dto = new IngresoMensaje();
        dto.setId(entity.getId());
        dto.setMensaje(entity.getMensaje());
        dto.setFecha(String.valueOf(entity.getFecha()));
        dto.setEsSistema(entity.getEsSistema());

        if (!entity.getEsSistema() && entity.getUsuario() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(entity.getUsuario().getId());
            usuario.setNombre(entity.getUsuario().getNombre());
            usuario.setApellidos(entity.getUsuario().getApellidos());
            dto.setUsuario(usuario);
        }

        return dto;
    }
}
