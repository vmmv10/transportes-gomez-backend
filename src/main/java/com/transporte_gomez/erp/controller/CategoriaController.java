package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.adapter.CategoriaAdapter;
import com.transporte_gomez.erp.dto.Categoria;
import com.transporte_gomez.erp.services.CategoriaService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping("/list")
    public List<Categoria> getAll() {
        return categoriaService.getAll();
    }
}
