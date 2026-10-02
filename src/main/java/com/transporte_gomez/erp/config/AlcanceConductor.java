package com.transporte_gomez.erp.config;

import com.transporte_gomez.erp.repository.EntregaRepository;
import com.transporte_gomez.erp.repository.RutaRepository;
import com.transporte_gomez.erp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * El usuario que solo tiene rol Conductor opera únicamente sus rutas y entregas.
 * Administrador y Operaciones no tienen restricción.
 */
@Component
@RequiredArgsConstructor
public class AlcanceConductor {

    private static final String MENSAJE = "Esta ruta no está asignada a ti";

    private final UsuarioRepository usuarioRepository;
    private final RutaRepository rutaRepository;
    private final EntregaRepository entregaRepository;

    /** Solo Conductor (sin Administrador ni Operaciones). */
    public boolean soloConductor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        Set<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        return roles.contains("ROLE_" + Roles.CONDUCTOR)
                && !roles.contains("ROLE_" + Roles.ADMINISTRADOR)
                && !roles.contains("ROLE_" + Roles.OPERACIONES);
    }

    @Transactional(readOnly = true)
    public void verificarRuta(Integer rutaId) {
        if (!soloConductor()) {
            return;
        }
        Long chofer = rutaRepository.findById(rutaId).map(r -> r.getChofer().getId()).orElse(null);
        if (chofer == null || !Objects.equals(chofer, usuarioId())) {
            throw new AccessDeniedException(MENSAJE);
        }
    }

    @Transactional(readOnly = true)
    public void verificarEntrega(Integer entregaId) {
        if (!soloConductor()) {
            return;
        }
        Long chofer = entregaRepository.findById(entregaId).map(e -> e.getRuta().getChofer().getId()).orElse(null);
        if (chofer == null || !Objects.equals(chofer, usuarioId())) {
            throw new AccessDeniedException(MENSAJE);
        }
    }

    /** El conductor debe comenzar la ruta (con odómetro de salida) antes de registrar entregas. */
    @Transactional(readOnly = true)
    public void verificarRutaComenzada(Integer entregaId) {
        if (!soloConductor()) {
            return;
        }
        boolean comenzada = entregaRepository.findById(entregaId)
                .map(e -> e.getRuta().getInicio() != null || Boolean.TRUE.equals(e.getRuta().getEnTransito()))
                .orElse(true);
        if (!comenzada) {
            throw new IllegalArgumentException("Comienza la ruta registrando el kilometraje de salida antes de entregar");
        }
    }

    private Long usuarioId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return usuarioRepository.findByAuth0Id(auth.getName()).map(u -> u.getId()).orElse(-1L);
    }
}
