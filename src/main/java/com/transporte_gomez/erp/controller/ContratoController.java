package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.config.AlcanceCliente;

import com.transporte_gomez.erp.dto.Contrato;
import com.transporte_gomez.erp.dto.ContratoFiltro;
import com.transporte_gomez.erp.services.ContratoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/contratos")
public class ContratoController {

    private final ContratoService contratoService;
    private final AlcanceCliente alcance;

    @GetMapping
    public Page<Contrato> getAll(ContratoFiltro filtro, Pageable pageable) {
        alcance.clienteRestringido().ifPresent(filtro::setCliente);
        return contratoService.getAll(pageable, filtro);
    }

    /** Contratos vigentes hoy, para selectores. ?cliente=ID opcional */
    @GetMapping("/vigentes")
    public List<Contrato> listarVigentes(@RequestParam(required = false) Long cliente) {
        return contratoService.listarVigentes(alcance.clienteRestringido().orElse(cliente));
    }

    @GetMapping("/{id}")
    public Contrato getById(@PathVariable Long id) {
        alcance.verificarContrato(id);
        return contratoService.getById(id);
    }

    @PostMapping
    public Contrato create(@RequestBody Contrato contrato) {
        return contratoService.create(contrato);
    }

    @PutMapping("/{id}")
    public Contrato update(@PathVariable Long id, @RequestBody Contrato contrato) {
        return contratoService.update(id, contrato);
    }

    @PutMapping("/{id}/desactivar")
    public void desactivar(@PathVariable Long id) {
        contratoService.cambiarEstado(id, false);
    }

    @PutMapping("/{id}/activar")
    public void activar(@PathVariable Long id) {
        contratoService.cambiarEstado(id, true);
    }

    /** Solo contratos sin órdenes de servicio asociadas. */
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        contratoService.eliminar(id);
    }
}
