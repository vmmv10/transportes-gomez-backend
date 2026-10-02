package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.config.AlcanceConductor;
import com.transporte_gomez.erp.dto.RutaCosto;
import com.transporte_gomez.erp.dto.RutaCostoFiltro;
import com.transporte_gomez.erp.dto.RutaCostoResumen;
import com.transporte_gomez.erp.services.RutaCostoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Costos de ruta: combustible, peajes, cruces, arriendo de lancha, viáticos. */
@RequiredArgsConstructor
@RestController
public class RutaCostoController {

    private final RutaCostoService rutaCostoService;
    private final AlcanceConductor alcanceConductor;

    @GetMapping("/api/rutas/{rutaId}/costos")
    public List<RutaCosto> listar(@PathVariable Integer rutaId) {
        alcanceConductor.verificarRuta(rutaId);
        return rutaCostoService.listar(rutaId);
    }

    @PostMapping("/api/rutas/{rutaId}/costos")
    public RutaCosto crear(@PathVariable Integer rutaId, @RequestBody RutaCosto costo, @AuthenticationPrincipal Jwt jwt) {
        alcanceConductor.verificarRuta(rutaId);
        return rutaCostoService.crear(rutaId, costo, jwt != null ? jwt.getSubject() : null);
    }

    @PutMapping("/api/rutas/{rutaId}/costos/{costoId}")
    public RutaCosto actualizar(@PathVariable Integer rutaId, @PathVariable Long costoId, @RequestBody RutaCosto costo) {
        return rutaCostoService.actualizar(rutaId, costoId, costo);
    }

    @DeleteMapping("/api/rutas/{rutaId}/costos/{costoId}")
    public void eliminar(@PathVariable Integer rutaId, @PathVariable Long costoId) {
        rutaCostoService.eliminar(rutaId, costoId);
    }

    /** Costo real por ruta. ?desde=yyyy-MM-dd&hasta=yyyy-MM-dd&vehiculo=&chofer=&conCostos=true */
    @GetMapping("/api/costos-rutas/resumen")
    public List<RutaCostoResumen> resumen(RutaCostoFiltro filtro) {
        return rutaCostoService.resumen(filtro);
    }
}
