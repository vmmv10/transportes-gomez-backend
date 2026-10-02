package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.config.AlcanceCliente;

import com.transporte_gomez.erp.dto.Destino;
import com.transporte_gomez.erp.dto.DestinoFiltro;
import com.transporte_gomez.erp.services.DestinoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/destinos")
public class DestinoController {

    private final DestinoService destinoService;
    private final AlcanceCliente alcance;

    @GetMapping
    public Page<Destino> getAll(DestinoFiltro filtro, Pageable pageable) {
        alcance.clienteRestringido().ifPresent(filtro::setCliente);
        return destinoService.getAll(pageable, filtro);
    }

    @GetMapping("/{id}")
    public Destino getById(@PathVariable Long id) {
        alcance.verificarDestino(id);
        return destinoService.getById(id);
    }

    @PostMapping
    public Destino create(@RequestBody Destino destino) {
        return destinoService.create(destino);
    }

    @PutMapping("/{id}")
    public Destino update(@PathVariable Long id, @RequestBody Destino destino) {
        return destinoService.update(id, destino);
    }

    @PutMapping("/{id}/desactivar")
    public void desactivar(@PathVariable Long id) {
        destinoService.cambiarEstado(id, false);
    }

    @PutMapping("/{id}/activar")
    public void activar(@PathVariable Long id) {
        destinoService.cambiarEstado(id, true);
    }
}
