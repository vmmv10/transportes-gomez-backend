package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.IngresosEmergencia;
import com.transporte_gomez.erp.dto.IngresosEmergenciaDetalle;
import com.transporte_gomez.erp.dto.IngresosEmergenciaFiltro;
import com.transporte_gomez.erp.enums.IngresoEmergenciaEstado;
import com.transporte_gomez.erp.services.IngresoEmergenciaService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/ingresos-emergencia")
public class IngresoEmergenciaController {
    
    private final IngresoEmergenciaService ingresoEmergenciaService;

    @GetMapping
    public Page<IngresosEmergencia> getAll(Pageable pageable, IngresosEmergenciaFiltro filtro) {
        return ingresoEmergenciaService.findAll(pageable, filtro);
    }

    @PostMapping
    public IngresosEmergencia create(@RequestBody IngresosEmergencia ingresosEmergencia) {
        return ingresoEmergenciaService.create(ingresosEmergencia);
    }

    @GetMapping("/{folio}/detalle/{codigo}")
    public IngresosEmergenciaDetalle createDetalle(@PathVariable Integer folio, @PathVariable String codigo) {
        return ingresoEmergenciaService.createDetalle(folio, codigo);
    }

    @GetMapping("/{folio}")
    public IngresosEmergencia getByFolio(@PathVariable Integer folio) {
        return ingresoEmergenciaService.getByFolio(folio);
    }

    @PutMapping("/detalles/{folio}/add")
    public void sumarCantidadDetalle(@PathVariable Integer folio, @RequestBody IngresosEmergenciaDetalle ingresosEmergenciaDetalle) {
        ingresoEmergenciaService.sumarCantidadDetalle(folio, ingresosEmergenciaDetalle.getCantidad());
    }

    @PutMapping("/{folio}/abrir")
    public void abrir(@PathVariable Integer folio) {
        ingresoEmergenciaService.updateEstado(folio, IngresoEmergenciaEstado.ABIERTO.getCodigo());
    }

    @PutMapping("/{folio}/cerrar")
    public void cerrar(@PathVariable Integer folio) {
        ingresoEmergenciaService.updateEstado(folio, IngresoEmergenciaEstado.CERRADO.getCodigo());
    }

    @PutMapping("/detalles/{id}/editar-cantidad")
    public void modificarCantidadDetalle(@PathVariable Integer id, @RequestBody IngresosEmergenciaDetalle ingresosEmergenciaDetalle) {
        ingresoEmergenciaService.modificarCantidadDetalle(id, ingresosEmergenciaDetalle.getCantidad());
    }

    @DeleteMapping("/detalles/{id}")
    public void eliminarDetalle(@PathVariable Integer id) {
        ingresoEmergenciaService.eliminarDetalle(id);
    }
}
