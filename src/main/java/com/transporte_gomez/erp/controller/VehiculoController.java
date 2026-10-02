package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Vehiculo;
import com.transporte_gomez.erp.dto.VehiculoFiltro;
import com.transporte_gomez.erp.enums.VehiculoTipo;
import com.transporte_gomez.erp.services.VehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @GetMapping
    public Page<Vehiculo> getAll(VehiculoFiltro filtro, Pageable pageable) {
        return vehiculoService.getAll(pageable, filtro);
    }

    /** Vehículos activos para selectores. ?tipo=LANCHA opcional */
    @GetMapping("/activos")
    public List<Vehiculo> listarActivos(@RequestParam(required = false) VehiculoTipo tipo) {
        return vehiculoService.listarActivos(tipo);
    }

    @GetMapping("/{id}")
    public Vehiculo getById(@PathVariable Long id) {
        return vehiculoService.getById(id);
    }

    @PostMapping
    public Vehiculo create(@RequestBody Vehiculo vehiculo) {
        return vehiculoService.create(vehiculo);
    }

    @PutMapping("/{id}")
    public Vehiculo update(@PathVariable Long id, @RequestBody Vehiculo vehiculo) {
        return vehiculoService.update(id, vehiculo);
    }

    @PutMapping("/{id}/activar")
    public void activar(@PathVariable Long id) {
        vehiculoService.cambiarActivo(id, true);
    }

    @PutMapping("/{id}/desactivar")
    public void desactivar(@PathVariable Long id) {
        vehiculoService.cambiarActivo(id, false);
    }

    /** Solo vehículos que no se han usado en rutas ni costos. */
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        vehiculoService.eliminar(id);
    }
}
