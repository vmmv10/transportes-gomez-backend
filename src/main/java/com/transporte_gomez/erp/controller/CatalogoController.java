package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Comuna;
import com.transporte_gomez.erp.dto.ServicioTipo;
import com.transporte_gomez.erp.services.ComunaService;
import com.transporte_gomez.erp.services.ServicioTipoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Catálogos de solo lectura para selectores. */
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class CatalogoController {

    private final ComunaService comunaService;
    private final ServicioTipoService servicioTipoService;

    @GetMapping("/comunas")
    public List<Comuna> comunas() {
        return comunaService.listar();
    }

    @GetMapping("/servicios-tipos")
    public List<ServicioTipo> serviciosTipos() {
        return servicioTipoService.listar();
    }
}
