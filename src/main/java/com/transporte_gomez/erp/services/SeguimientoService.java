package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.RutaCostoAdapter;
import com.transporte_gomez.erp.dto.SeguimientoEvento;
import com.transporte_gomez.erp.entity.EntregaEntity;
import com.transporte_gomez.erp.entity.SeguimientoEventoEntity;
import com.transporte_gomez.erp.entity.UsuarioEntity;
import com.transporte_gomez.erp.enums.SeguimientoTipo;
import com.transporte_gomez.erp.repository.SeguimientoEventoRepository;
import com.transporte_gomez.erp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/** Historial de cada orden de servicio. */
@Service
@RequiredArgsConstructor
public class SeguimientoService {

    private final SeguimientoEventoRepository repository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void registrar(EntregaEntity entrega, SeguimientoTipo tipo, String descripcion,
                          BigDecimal latitud, BigDecimal longitud, BigDecimal distanciaKm) {
        SeguimientoEventoEntity e = new SeguimientoEventoEntity();
        e.setOrdenServicio(entrega.getOrdenServicio());
        e.setEntregaId(entrega.getId());
        e.setRutaId(entrega.getRuta() != null ? entrega.getRuta().getId() : null);
        e.setTipo(tipo);
        e.setDescripcion(descripcion);
        e.setLatitud(latitud);
        e.setLongitud(longitud);
        e.setDistanciaKm(distanciaKm);
        e.setUsuario(usuarioActual());
        repository.save(e);
    }

    /** Solo una señal de "cerca del destino" por entrega y ruta. */
    public boolean yaRegistrado(EntregaEntity entrega, SeguimientoTipo tipo) {
        return repository.existsByEntregaIdAndRutaIdAndTipo(entrega.getId(), entrega.getRuta().getId(), tipo);
    }

    @Transactional(readOnly = true)
    public List<SeguimientoEvento> historial(Long ordenServicioId) {
        return repository.findByOrdenServicio_IdOrderByFechaAscIdAsc(ordenServicioId).stream().map(e -> {
            SeguimientoEvento dto = new SeguimientoEvento();
            dto.setId(e.getId());
            dto.setOrdenServicioId(ordenServicioId);
            dto.setEntregaId(e.getEntregaId());
            dto.setRutaId(e.getRutaId());
            dto.setTipo(e.getTipo());
            dto.setDescripcion(e.getDescripcion());
            dto.setLatitud(e.getLatitud());
            dto.setLongitud(e.getLongitud());
            dto.setDistanciaKm(e.getDistanciaKm());
            dto.setVisiblePublico(e.getVisiblePublico());
            dto.setUsuarioNombre(RutaCostoAdapter.nombre(e.getUsuario()));
            dto.setFecha(e.getFecha());
            return dto;
        }).toList();
    }

    private UsuarioEntity usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            return null;
        }
        return usuarioRepository.findByAuth0Id(auth.getName()).orElse(null);
    }
}
