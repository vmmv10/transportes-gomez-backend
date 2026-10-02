package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.config.AlcanceConductor;
import com.transporte_gomez.erp.config.Roles;
import com.transporte_gomez.erp.dto.Entrega;
import com.transporte_gomez.erp.dto.Ruta;
import com.transporte_gomez.erp.dto.RutaFiltro;
import com.transporte_gomez.erp.dto.Usuario;
import com.transporte_gomez.erp.services.RutaService;
import com.transporte_gomez.erp.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/rutas")
public class RutaController {

    private final UsuarioService usuarioService;
    private final RutaService rutaService;
    private final AlcanceConductor alcanceConductor;

    @GetMapping()
    public Page<Ruta> findAll(RutaFiltro filtro, Pageable pageable, @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = usuarioService.obtenerUsuarioLogeado(jwt);
        log.info("Usuario logeado: {}", usuario);
        // El conductor solo ve sus rutas (rol de Auth0, o el rol antiguo "repartidor")
        if (soloConductor() || (usuario.getRol() != null && usuario.getRol().equalsIgnoreCase("repartidor") && !esInterno())) {
            filtro.setChofer(usuario.getId());
        }
        return rutaService.findAll(pageable, filtro);
    }

    private static boolean tieneRol(String rol) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + rol));
    }

    private static boolean esInterno() {
        return tieneRol(Roles.ADMINISTRADOR) || tieneRol(Roles.OPERACIONES);
    }

    private static boolean soloConductor() {
        return tieneRol(Roles.CONDUCTOR) && !esInterno();
    }

    @GetMapping("/{id}")
    public Ruta findById(@PathVariable Integer id) {
        alcanceConductor.verificarRuta(id);
        return rutaService.findById(id);
    }

    @PostMapping()
    public Ruta create(@RequestBody Ruta ruta) {
        return rutaService.create(ruta);
    }

    @PutMapping("/{id}")
    public Ruta update(@PathVariable Integer id, @RequestBody Ruta ruta) {
        return rutaService.update(id, ruta);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        rutaService.delete(id);
    }

    @DeleteMapping("/{id}/ordenes-servicios/{ordenServicio}")
    public void deleteEntrega(@PathVariable Integer id, @PathVariable Long ordenServicio) {
        rutaService.deleteEntrega(id, ordenServicio);
    }

    @GetMapping("/fecha-hoy")
    public Ruta findByFechaAndChoferId(@AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = usuarioService.obtenerUsuarioLogeado(jwt);
        return rutaService.obtenerRutaUsuarioAndFechaHoy(usuario);
    }

    /** Cuerpo opcional: {"kmSalida": 123456} */
    @PutMapping("/{id}/comenzar")
    public Ruta comenzar(@PathVariable Integer id, @RequestBody(required = false) Ruta ruta) {
        alcanceConductor.verificarRuta(id);
        return rutaService.comenzarRuta(id, ruta != null ? ruta.getKmSalida() : null);
    }

    /** {"kilometros": 120} o {"kmSalida": 123456, "kmLlegada": 123576} */
    @PutMapping("/{id}/kilometros")
    public void updateKilometros(@PathVariable Integer id, @RequestBody Ruta ruta) {
        alcanceConductor.verificarRuta(id);
        rutaService.actualizarKilometros(id, ruta);
    }

    /** Cuerpo opcional: {"kmLlegada": 123576} */
    @PutMapping("/{id}/finalizar")
    public void finalizarRuta(@PathVariable Integer id, @RequestBody(required = false) Ruta ruta) {
        alcanceConductor.verificarRuta(id);
        rutaService.finalizarRuta(id, ruta != null ? ruta.getKmLlegada() : null);
    }
}
