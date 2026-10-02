package com.transporte_gomez.erp.config;

import com.transporte_gomez.erp.entity.ClienteEntity;
import com.transporte_gomez.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Alcance de los datos para el usuario con rol Cliente: solo ve y modifica lo de su organización
 * (usuarios.cliente_id). Los roles internos (Administrador, Operaciones, Bodega, Conductor) no tienen restricción.
 *
 * Un Cliente sin organización asignada no ve nada (se usa un id que no existe).
 */
@Component
@RequiredArgsConstructor
public class AlcanceCliente {

    /** id que no existe: un Cliente sin organización no ve registros */
    private static final Long SIN_ORGANIZACION = -1L;

    private static final Set<String> ROLES_INTERNOS = Set.of(
            "ROLE_" + Roles.ADMINISTRADOR, "ROLE_" + Roles.OPERACIONES, "ROLE_" + Roles.BODEGA, "ROLE_" + Roles.CONDUCTOR);

    private final UsuarioRepository usuarioRepository;
    private final IngresosRepository ingresosRepository;
    private final IngresosDetalleRepository ingresosDetalleRepository;
    private final OrdenServicioRepository ordenServicioRepository;
    private final EscuelaRepository escuelaRepository;
    private final DestinoRepository destinoRepository;
    private final ContratoRepository contratoRepository;

    /**
     * Cliente al que está limitado el usuario actual.
     * Vacío si el usuario es interno (ve todo).
     */
    public Optional<Long> clienteRestringido() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof JwtAuthenticationToken jwt)) {
            return Optional.empty();
        }
        Set<String> roles = jwt.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        if (roles.stream().anyMatch(ROLES_INTERNOS::contains) || !roles.contains("ROLE_" + Roles.CLIENTE)) {
            return Optional.empty();
        }
        Long clienteId = usuarioRepository.findByAuth0Id(jwt.getToken().getSubject())
                .map(u -> u.getCliente() != null ? u.getCliente().getId() : null)
                .orElse(null);
        return Optional.of(clienteId != null ? clienteId : SIN_ORGANIZACION);
    }

    public boolean esRestringido() {
        return clienteRestringido().isPresent();
    }

    // ---- Verificación de un registro puntual (403 si no es de su organización) ----

    public void verificarCliente(Long clienteId) {
        clienteRestringido().ifPresent(propio -> {
            if (!Objects.equals(propio, clienteId)) {
                throw sinAcceso();
            }
        });
    }

    public void verificarIngreso(Integer folio) {
        if (esRestringido()) {
            verificarCliente(ingresosRepository.findById(folio).map(i -> id(i.getCliente())).orElse(null));
        }
    }

    public void verificarDetalleIngreso(Integer detalleId) {
        if (esRestringido()) {
            verificarCliente(ingresosDetalleRepository.findById(detalleId)
                    .map(d -> d.getIngreso() != null ? id(d.getIngreso().getCliente()) : null)
                    .orElse(null));
        }
    }

    public void verificarOrden(Long ordenId) {
        if (esRestringido()) {
            verificarCliente(ordenServicioRepository.findById(ordenId).map(o -> id(o.getCliente())).orElse(null));
        }
    }

    public void verificarEscuela(Long escuelaId) {
        if (esRestringido()) {
            verificarCliente(escuelaRepository.findById(escuelaId).map(e -> id(e.getCliente())).orElse(null));
        }
    }

    public void verificarDestino(Long destinoId) {
        if (esRestringido()) {
            verificarCliente(destinoRepository.findById(destinoId).map(d -> id(d.getCliente())).orElse(null));
        }
    }

    public void verificarContrato(Long contratoId) {
        if (esRestringido()) {
            verificarCliente(contratoRepository.findById(contratoId).map(c -> id(c.getCliente())).orElse(null));
        }
    }

    private static Long id(ClienteEntity cliente) {
        return cliente != null ? cliente.getId() : null;
    }

    private static AccessDeniedException sinAcceso() {
        return new AccessDeniedException("Este registro no pertenece a tu organización");
    }
}
