package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.config.AlcanceCliente;
import com.transporte_gomez.erp.dto.SeguimientoEvento;
import com.transporte_gomez.erp.services.SeguimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Historial de una orden de servicio: en ruta, cerca del destino, entregada, no entregada... */
@RequiredArgsConstructor
@RestController
public class SeguimientoController {

    private final SeguimientoService seguimientoService;
    private final AlcanceCliente alcance;

    @GetMapping("/api/ordenes-servicios/{id}/seguimiento")
    public List<SeguimientoEvento> historial(@PathVariable Long id) {
        alcance.verificarOrden(id);
        return seguimientoService.historial(id);
    }
}
