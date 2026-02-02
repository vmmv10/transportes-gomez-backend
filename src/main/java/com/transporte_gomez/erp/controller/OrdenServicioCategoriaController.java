package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.OrdenServicioCategoria;
import com.transporte_gomez.erp.services.OrdenServicioCategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/ordenes-servicio-categorias")
public class OrdenServicioCategoriaController {

    private final OrdenServicioCategoriaService ordenServicioCategoriaService;

    @GetMapping
    public Page<OrdenServicioCategoria> getOrdenServicios(Pageable pageable, OrdenServicioCategoria filtro){
        return ordenServicioCategoriaService.findAll(pageable, filtro);
    }

    @GetMapping("/list")
    public List<OrdenServicioCategoria> getAll(){
        return ordenServicioCategoriaService.findAll();
    }

    @GetMapping("/{id}")
    public OrdenServicioCategoria getById(@PathVariable Integer id){
        return ordenServicioCategoriaService.findById(id);
    }

    @PostMapping
    public OrdenServicioCategoria create(@RequestBody OrdenServicioCategoria ordenServicioCategoria){
        return ordenServicioCategoriaService.createCategoria(ordenServicioCategoria);
    }

    @PutMapping("/{id}")
    public OrdenServicioCategoria update(@PathVariable Integer id, @RequestBody OrdenServicioCategoria ordenServicioCategoria){
        return ordenServicioCategoriaService.updateCategoria(id, ordenServicioCategoria);
    }

    @PutMapping("/{id}/desactive")
    public void desactive(@PathVariable Integer id){
        ordenServicioCategoriaService.desactivarCategoria(id);
    }

    @PutMapping("/{id}/active")
    public void active(@PathVariable Integer id){
        ordenServicioCategoriaService.activarCategoria(id);
    }
}
