package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.config.AlcanceCliente;
import com.transporte_gomez.erp.dto.Cliente;

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
    private final AlcanceCliente alcance;

    @GetMapping
    public Page<Ingresos> getAll(Pageable pageable, IngresosFiltro filtro) {
        alcance.clienteRestringido().ifPresent(filtro::setCliente);
        return ingresoService.findAll(pageable, filtro);
    }

    @PostMapping
    public Ingresos create(@RequestBody Ingresos ingresosEmergencia, @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = usuarioService.obtenerUsuarioLogeado(jwt);
        // El usuario Cliente solo registra ingresos de su organización
        alcance.clienteRestringido().ifPresent(clienteId -> {
            Cliente cliente = new Cliente();
            cliente.setId(clienteId);
            ingresosEmergencia.setCliente(cliente);
        });
        return ingresoService.create(ingresosEmergencia, usuario);
    }

    @GetMapping("/{folio}/detalle/{codigo}")
    public IngresosDetalle createDetalle(@PathVariable Integer folio, @PathVariable String codigo) {
        alcance.verificarIngreso(folio);
        return ingresoService.createDetalle(folio, codigo);
    }

    @GetMapping("/{folio}")
    public Ingresos getByFolio(@PathVariable Integer folio) {
        alcance.verificarIngreso(folio);
        return ingresoService.getByFolio(folio);
    }

    @PutMapping("/detalles/{folio}/add")
    public void sumarCantidadDetalle(@PathVariable Integer folio, @RequestBody IngresosDetalle ingresosEmergenciaDetalle) {
        alcance.verificarDetalleIngreso(folio);
        ingresoService.sumarCantidadDetalle(folio, ingresosEmergenciaDetalle.getCantidad());
    }

    @PutMapping("/{folio}/abrir")
    public void abrir(@PathVariable Integer folio) {
        alcance.verificarIngreso(folio);
        ingresoService.updateEstado(folio, IngresoEstado.ABIERTO.getCodigo());
    }

    @PutMapping("/{folio}/cerrar")
    public void cerrar(@PathVariable Integer folio) {
        alcance.verificarIngreso(folio);
        ingresoService.updateEstado(folio, IngresoEstado.CERRADO.getCodigo());
    }

    @PutMapping("/{folio}/habilitar")
    public void habilitar(@PathVariable Integer folio) {
        alcance.verificarIngreso(folio);
        ingresoService.updateEstado(folio, IngresoEstado.HABILITADO.getCodigo());
    }

    @PutMapping("/{folio}/inhabilitar")
    public void inhabilitar(@PathVariable Integer folio) {
        alcance.verificarIngreso(folio);
        ingresoService.updateEstado(folio, IngresoEstado.INHABILITADO.getCodigo());
    }

    @PutMapping("/detalles/{id}/editar-cantidad")
    public void modificarCantidadDetalle(@PathVariable Integer id, @RequestBody IngresosDetalle ingresosEmergenciaDetalle) {
        alcance.verificarDetalleIngreso(id);
        ingresoService.modificarCantidadDetalle(id, ingresosEmergenciaDetalle.getCantidad());
    }

    @DeleteMapping("/detalles/{id}")
    public void eliminarDetalle(@PathVariable Integer id) {
        alcance.verificarDetalleIngreso(id);
        ingresoService.eliminarDetalle(id);
    }

    @GetMapping("/temporal")
    public Ingresos getByFolio(@AuthenticationPrincipal Jwt jwt) {
        return ingresoService.getTemporalByUser(usuarioService.obtenerUsuarioLogeado(jwt));
    }

    @DeleteMapping("/{folio}")
    public void eliminarIngreso(@PathVariable Integer folio) {
        alcance.verificarIngreso(folio);
        ingresoService.delete(folio);
    }

    @GetMapping("/{id}/conversacion")
    public IngresoConversacion getConversacion(@PathVariable Integer id) {
        alcance.verificarIngreso(id);
        return ingresoService.getConversacion(id);
    }

    @PostMapping("/{id}/conversacion/mensaje")
    public IngresoMensaje crearMensaje(@PathVariable Integer id, @RequestBody IngresoMensaje mensaje, @AuthenticationPrincipal Jwt jwt) {
        alcance.verificarIngreso(id);
        Usuario usuario = usuarioService.obtenerUsuarioLogeado(jwt);
        return ingresoService.crearMensaje(id, mensaje, usuario);
    }
}
