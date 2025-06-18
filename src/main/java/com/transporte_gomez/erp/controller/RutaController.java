package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Ruta;
import com.transporte_gomez.erp.dto.RutaFiltro;
import com.transporte_gomez.erp.services.RutaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    private final RutaService rutaService;

    @GetMapping()
    public Page<Ruta> findAll(RutaFiltro filtro, Pageable pageable) {
        return rutaService.findAll(pageable, filtro);
    }

    @GetMapping("/{id}")
    public Ruta findById(@PathVariable Integer id) {
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

}
