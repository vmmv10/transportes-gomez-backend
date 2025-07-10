package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Devolucion;
import com.transporte_gomez.erp.dto.DevolucionDetalle;
import com.transporte_gomez.erp.dto.DevolucionFiltro;
import com.transporte_gomez.erp.services.DevolucionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/devoluciones")
public class DevolucionController {

    private final DevolucionService devolucionService;

    @GetMapping
    public Page<Devolucion> getDevoluciones(Pageable pageable, DevolucionFiltro filtro) {
        return devolucionService.findAll(pageable, filtro);
    }

    @PostMapping
    public Devolucion createDevolucion(@RequestBody Devolucion devolucion) {
        return devolucionService.create(devolucion);
    }

    @GetMapping("/{folio}/detalle/{codigo}")
    public DevolucionDetalle createDetalle(@PathVariable Long folio, @PathVariable String codigo) {
        return devolucionService.createDetalle(folio, codigo);
    }

    @GetMapping("/{folio}")
    public Devolucion getByFolio(@PathVariable Long folio) {
        return devolucionService.getByFolio(folio);
    }

    @PutMapping("/detalles/{folio}/add")
    public void sumarCantidadDetalle(@PathVariable Long folio, @RequestBody DevolucionDetalle devolucionDetalle) {
        devolucionService.sumarCantidadDetalle(folio, devolucionDetalle.getCantidad());
    }
}
