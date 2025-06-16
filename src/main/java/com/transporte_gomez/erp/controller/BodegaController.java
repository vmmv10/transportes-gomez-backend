package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Bodega;
import com.transporte_gomez.erp.services.BodegaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bodegas")
public class BodegaController {

    private final BodegaService bodegaService;

    @GetMapping
    public List<Bodega> getAllBodegas() {
        return bodegaService.getBodegas();
    }

    @GetMapping("/{id}")
    public Bodega getBodegaById(@PathVariable Long id) {
        return bodegaService.getBodegaById(id);
    }

    @PostMapping()
    public Bodega createBodega(@RequestBody Bodega bodega) {
        return bodegaService.createBodega(bodega);
    }

    @PutMapping("/{id}")
    public Bodega updateBodega(@PathVariable Long id, @RequestBody Bodega bodega) {
        return bodegaService.updateBodega(id, bodega);
    }

    @DeleteMapping("/{id}")
    public void deleteBodega(@PathVariable Long id) {
        bodegaService.deleteBodega(id);
    }

    @PutMapping("/{id}/desactivate")
    public void desactivateBodega(@PathVariable Long id) {
        bodegaService.desactivateBodega(id);
    }
}
