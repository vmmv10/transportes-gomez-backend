package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Bulto;
import com.transporte_gomez.erp.services.BultoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class BultoController {

    private final BultoService bultoService;

    @GetMapping("/ingresos/{folio}/bultos")
    public List<Bulto> listarPorIngreso(@PathVariable Integer folio) {
        return bultoService.listarPorIngreso(folio);
    }

    @PostMapping("/ingresos/{folio}/bultos")
    public Bulto agregar(@PathVariable Integer folio, @RequestBody Bulto bulto) {
        return bultoService.agregar(folio, bulto);
    }

    /** Buscar por N° de seguimiento externo: /api/bultos?codigoExterno=XXXX */
    @GetMapping("/bultos")
    public List<Bulto> buscar(@RequestParam String codigoExterno) {
        return bultoService.buscarPorCodigoExterno(codigoExterno);
    }

    @PutMapping("/bultos/{id}")
    public Bulto actualizar(@PathVariable Long id, @RequestBody Bulto bulto) {
        return bultoService.actualizar(id, bulto);
    }

    @DeleteMapping("/bultos/{id}")
    public void eliminar(@PathVariable Long id) {
        bultoService.eliminar(id);
    }
}
