package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.dto.MensajeContacto;
import com.transporte_gomez.erp.dto.SolicitudPublica;
import com.transporte_gomez.erp.entity.MensajeContactoEntity;
import com.transporte_gomez.erp.repository.MensajeContactoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MensajeContactoService {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final MensajeContactoRepository repository;
    private final NotificacionCorreoService notificaciones;

    @Transactional
    public void crearDesdeLanding(SolicitudPublica s, String ip) {
        String nombre = ComercialReferencias.texto(s.getNombre());
        String email = ComercialReferencias.texto(s.getEmail());
        String mensaje = ComercialReferencias.texto(s.getMensaje());
        if (nombre == null) {
            throw new IllegalArgumentException("Ingresa tu nombre");
        }
        if (email == null || !EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("Ingresa un correo válido");
        }
        if (mensaje == null) {
            throw new IllegalArgumentException("Escribe tu mensaje");
        }
        MensajeContactoEntity e = new MensajeContactoEntity();
        e.setNombre(corta(nombre, 200));
        e.setEmail(corta(email, 200));
        e.setTelefono(corta(ComercialReferencias.texto(s.getTelefono()), 40));
        e.setMensaje(corta(mensaje, 3000));
        e.setMotivo(com.transporte_gomez.erp.enums.MensajeMotivo.desde(s.getMotivo()));
        String codigo = ComercialReferencias.texto(s.getCodigoSeguimiento());
        if (codigo != null) {
            codigo = codigo.toUpperCase().replaceAll("[^0-9A-Z]", "");
            e.setCodigoSeguimiento(codigo.isEmpty() ? null : corta(codigo, 12));
        }
        e.setIp(ip);
        notificaciones.mensajeRecibido(repository.save(e));
    }

    @Transactional(readOnly = true)
    public Page<MensajeContacto> getAll(Boolean atendido, com.transporte_gomez.erp.enums.MensajeMotivo motivo, Pageable pageable) {
        Page<MensajeContactoEntity> page;
        if (motivo != null) {
            page = atendido == null ? repository.findByMotivo(motivo, pageable) : repository.findByAtendidoAndMotivo(atendido, motivo, pageable);
        } else {
            page = atendido == null ? repository.findAll(pageable) : repository.findByAtendido(atendido, pageable);
        }
        return page.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public long pendientes() {
        return repository.countByAtendidoFalse();
    }

    @Transactional
    public void marcarAtendido(Long id, boolean atendido) {
        MensajeContactoEntity e = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensaje no encontrado con ID: " + id));
        e.setAtendido(atendido);
        repository.save(e);
    }

    private MensajeContacto toDto(MensajeContactoEntity e) {
        MensajeContacto d = new MensajeContacto();
        d.setId(e.getId());
        d.setNombre(e.getNombre());
        d.setEmail(e.getEmail());
        d.setTelefono(e.getTelefono());
        d.setMensaje(e.getMensaje());
        d.setMotivo(e.getMotivo());
        d.setMotivoTexto(e.getMotivo() != null ? e.getMotivo().getTexto() : null);
        d.setCodigoSeguimiento(e.getCodigoSeguimiento());
        d.setAtendido(e.getAtendido());
        d.setFechaCreacion(e.getFechaCreacion());
        return d;
    }

    private static String corta(String t, int max) {
        return t == null ? null : t.length() > max ? t.substring(0, max) : t;
    }
}
