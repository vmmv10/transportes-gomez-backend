package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.*;
import com.transporte_gomez.erp.enums.IngresoEstado;
import com.transporte_gomez.erp.services.IngresoService;
import com.transporte_gomez.erp.services.UsuarioService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/ingresos")
public class IngresoController {
    
    private final IngresoService ingresoService;
    private final UsuarioService usuarioService;

    @GetMapping
    public Page<Ingresos> getAll(Pageable pageable, IngresosFiltro filtro) {
        return ingresoService.findAll(pageable, filtro);
    }

    @PostMapping
    public Ingresos create(@RequestBody Ingresos ingresosEmergencia, @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = usuarioService.obtenerUsuarioLogeado(jwt);
        return ingresoService.create(ingresosEmergencia, usuario);
    }

    @GetMapping("/{folio}/detalle/{codigo}")
    public IngresosDetalle createDetalle(@PathVariable Integer folio, @PathVariable String codigo) {
        return ingresoService.createDetalle(folio, codigo);
    }

    @GetMapping("/{folio}")
    public Ingresos getByFolio(@PathVariable Integer folio) {
        return ingresoService.getByFolio(folio);
    }

    @PutMapping("/detalles/{folio}/add")
    public void sumarCantidadDetalle(@PathVariable Integer folio, @RequestBody IngresosDetalle ingresosEmergenciaDetalle) {
        ingresoService.sumarCantidadDetalle(folio, ingresosEmergenciaDetalle.getCantidad());
    }

    @PutMapping("/{folio}/abrir")
    public void abrir(@PathVariable Integer folio) {
        ingresoService.updateEstado(folio, IngresoEstado.ABIERTO.getCodigo());
    }

    @PutMapping("/{folio}/cerrar")
    public void cerrar(@PathVariable Integer folio) {
        ingresoService.updateEstado(folio, IngresoEstado.CERRADO.getCodigo());
    }

    @PutMapping("/{folio}/habilitar")
    public void habilitar(@PathVariable Integer folio) {
        ingresoService.updateEstado(folio, IngresoEstado.HABILITADO.getCodigo());
    }

    @PutMapping("/{folio}/inhabilitar")
    public void inhabilitar(@PathVariable Integer folio) {
        ingresoService.updateEstado(folio, IngresoEstado.INHABILITADO.getCodigo());
    }

    @PutMapping("/detalles/{id}/editar-cantidad")
    public void modificarCantidadDetalle(@PathVariable Integer id, @RequestBody IngresosDetalle ingresosEmergenciaDetalle) {
        ingresoService.modificarCantidadDetalle(id, ingresosEmergenciaDetalle.getCantidad());
    }

    @DeleteMapping("/detalles/{id}")
    public void eliminarDetalle(@PathVariable Integer id) {
        ingresoService.eliminarDetalle(id);
    }

    @GetMapping("/temporal")
    public Ingresos getByFolio(@AuthenticationPrincipal Jwt jwt) {
        return ingresoService.getTemporalByUser(usuarioService.obtenerUsuarioLogeado(jwt));
    }

    @DeleteMapping("/{folio}")
    public void eliminarIngreso(@PathVariable Integer folio) {
        ingresoService.delete(folio);
    }

    @GetMapping("/{id}/conversacion")
    public IngresoConversacion getConversacion(@PathVariable Integer id) {
        return ingresoService.getConversacion(id);
    }

    @PostMapping("/{id}/conversacion/mensaje")
    public IngresoMensaje crearMensaje(@PathVariable Integer id, @RequestBody IngresoMensaje mensaje, @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = usuarioService.obtenerUsuarioLogeado(jwt);
        return ingresoService.crearMensaje(id, mensaje, usuario);
    }
}
