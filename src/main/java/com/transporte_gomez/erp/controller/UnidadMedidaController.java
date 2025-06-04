package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.UnidadMedida;
import com.transporte_gomez.erp.services.UnidadMedidaService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/unidad-medida")
public class UnidadMedidaController {

    private final UnidadMedidaService unidadMedidaService;

    @GetMapping()
    public List<UnidadMedida> getUnidadMedida() {
        return unidadMedidaService.findAll();
    }
}
