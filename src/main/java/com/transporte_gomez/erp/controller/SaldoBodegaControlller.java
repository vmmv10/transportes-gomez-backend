package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.SaldoBodega;
import com.transporte_gomez.erp.dto.SaldoBodegaFiltro;
import com.transporte_gomez.erp.services.SaldoBodegaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/saldo-bodega")
public class SaldoBodegaControlller {

    private final SaldoBodegaService saldoBodegaService;

    @GetMapping()
    public Page<SaldoBodega> findAll(Pageable pageable, SaldoBodegaFiltro filtro) {
        return saldoBodegaService.findAll(filtro, pageable);
    }

    @GetMapping("/codigo/{codigo}")
    public SaldoBodega findByCodigo(@PathVariable String codigo) {
        return saldoBodegaService.findByCodigo(codigo);
    }

    @GetMapping("/{id}/bodega/{bodegaId}")
    public SaldoBodega getSaldoBodegaById(@PathVariable Long id, @PathVariable Long bodegaId) {
        return saldoBodegaService.getSaldoBodegaById(id, bodegaId);
    }
}
