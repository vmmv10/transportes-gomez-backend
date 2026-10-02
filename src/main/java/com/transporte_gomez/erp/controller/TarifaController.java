package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Tarifa;
import com.transporte_gomez.erp.dto.TarifaFiltro;
import com.transporte_gomez.erp.dto.TarifaSugerida;
import com.transporte_gomez.erp.services.TarifaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tarifas")
public class TarifaController {

    private final TarifaService tarifaService;

    @GetMapping
    public Page<Tarifa> getAll(TarifaFiltro filtro, Pageable pageable) {
        return tarifaService.getAll(pageable, filtro);
    }

    /** Tarifa aplicable y monto: ?servicio=&unidad=&cantidad=&cliente=&comunaOrigen=&comunaDestino= */
    @GetMapping("/sugerir")
    public TarifaSugerida sugerir(@RequestParam(required = false) Long cliente, @RequestParam Integer servicio,
                                  @RequestParam(required = false) Integer comunaOrigen, @RequestParam(required = false) Integer comunaDestino,
                                  @RequestParam Integer unidad, @RequestParam(required = false) BigDecimal cantidad) {
        return tarifaService.sugerir(cliente, servicio, comunaOrigen, comunaDestino, unidad, cantidad);
    }

    @GetMapping("/{id}")
    public Tarifa getById(@PathVariable Long id) {
        return tarifaService.getById(id);
    }

    @PostMapping
    public Tarifa create(@RequestBody Tarifa tarifa) {
        return tarifaService.create(tarifa);
    }

    @PutMapping("/{id}")
    public Tarifa update(@PathVariable Long id, @RequestBody Tarifa tarifa) {
        return tarifaService.update(id, tarifa);
    }

    @PutMapping("/{id}/activar")
    public void activar(@PathVariable Long id) {
        tarifaService.cambiarActivo(id, true);
    }

    @PutMapping("/{id}/desactivar")
    public void desactivar(@PathVariable Long id) {
        tarifaService.cambiarActivo(id, false);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        tarifaService.eliminar(id);
    }
}
