package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Marca;
import com.transporte_gomez.erp.entity.CategoriaEntity;
import com.transporte_gomez.erp.services.MarcaService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/marcas")
public class MarcaController {

    private final MarcaService marcaService;

    @GetMapping("/list")
    public List<Marca> getAll() {
        return marcaService.getAll();
    }
}
