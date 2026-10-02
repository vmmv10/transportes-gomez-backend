package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.config.AlcanceCliente;
import org.springframework.data.domain.PageImpl;

import com.transporte_gomez.erp.dto.Cliente;
import com.transporte_gomez.erp.dto.ClienteFiltro;
import com.transporte_gomez.erp.services.ClienteService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final AlcanceCliente alcance;

    @GetMapping
    public Page<Cliente> getAll(ClienteFiltro filtro, Pageable pageable) {
        // El usuario Cliente solo ve su propia organización
        if (alcance.esRestringido()) {
            return new PageImpl<>(listarActivos(), pageable, listarActivos().size());
        }
        return clienteService.getAll(pageable, filtro);
    }

    /** Clientes activos, para selectores */
    @GetMapping("/list")
    public List<Cliente> listarActivos() {
        return alcance.clienteRestringido()
                .map(clienteId -> clienteService.listarActivos().stream().filter(c -> clienteId.equals(c.getId())).toList())
                .orElseGet(clienteService::listarActivos);
    }

    @GetMapping("/{id}")
    public Cliente getById(@PathVariable Long id) {
        alcance.verificarCliente(id);
        return clienteService.getById(id);
    }

    @PostMapping
    public Cliente create(@RequestBody Cliente cliente) {
        return clienteService.create(cliente);
    }

    @PutMapping("/{id}")
    public Cliente update(@PathVariable Long id, @RequestBody Cliente cliente) {
        return clienteService.update(id, cliente);
    }

    @PutMapping("/{id}/desactivar")
    public void desactivar(@PathVariable Long id) {
        clienteService.cambiarEstado(id, false);
    }

    @PutMapping("/{id}/activar")
    public void activar(@PathVariable Long id) {
        clienteService.cambiarEstado(id, true);
    }
}
