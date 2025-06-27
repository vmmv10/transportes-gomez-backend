package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.EntregaFiltro;
import com.transporte_gomez.erp.dto.ReporteMes;
import com.transporte_gomez.erp.services.EntregaServices;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/entregas")
public class EntregaController {

    private final EntregaServices entregaServices;

    @GetMapping
    public Object findAll(Pageable pageable, EntregaFiltro filtro) {
        return entregaServices.getEntregas(pageable, filtro);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        entregaServices.deleteEntrega(id);
    }

    @GetMapping("/reporte/mes")
    public List<ReporteMes> obtenerEntregasPorMes() {
        return entregaServices.obtenerEntregasEntregadasPorMes();
    }
}
