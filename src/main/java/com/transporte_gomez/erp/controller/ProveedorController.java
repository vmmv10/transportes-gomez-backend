package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Proveedor;
import com.transporte_gomez.erp.entity.ProveedorEntity;
import com.transporte_gomez.erp.services.ProveedorService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public List<Proveedor> getProveedores() {
        return proveedorService.getProveedores();
    }

    @GetMapping("/{id}")
    public Proveedor getProveedorById(@PathVariable Long id) {
        return proveedorService.getProveedorById(id);
    }

    @PostMapping
    public Proveedor createProveedor(@RequestBody Proveedor proveedor) {
        return proveedorService.createProveedor(proveedor);
    }

    @PutMapping("/{id}")
    public Proveedor updateProveedor(@PathVariable Long id, @RequestBody Proveedor proveedor) {
        return proveedorService.updateProveedor(id, proveedor);
    }

    @PutMapping("/{id}/desactivar")
    public void desactivateProveedor(@PathVariable Long id) {
        proveedorService.desactivateProveedor(id);
    }
}
