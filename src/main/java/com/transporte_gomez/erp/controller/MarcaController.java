package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Marca;
import com.transporte_gomez.erp.dto.MarcaFiltro;
import com.transporte_gomez.erp.services.MarcaService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/marcas")
public class MarcaController {

    private final MarcaService marcaService;

    @GetMapping("/list")
    public List<Marca> getAll() {
        return marcaService.getList();
    }

    @GetMapping
    public Page<Marca> getAllMarcas(MarcaFiltro filtro, Pageable pageable) {
        return marcaService.findAll(filtro, pageable);
    }

    @GetMapping("/{id}")
    public Marca getMarcaById(@PathVariable Integer id) {
        return marcaService.getById(id);
    }

    @PostMapping()
    public Marca createMarca(@RequestBody Marca marca) {
        return marcaService.create(marca);
    }

    @PutMapping("/{id}")
    public Marca updateMarca(@PathVariable Integer id, @RequestBody Marca marca) {
        return marcaService.update(id, marca);
    }

    @PutMapping("/{id}/desactive")
    public void desactiveMarca(@PathVariable Integer id) {
        marcaService.desactive(id);
    }
}
