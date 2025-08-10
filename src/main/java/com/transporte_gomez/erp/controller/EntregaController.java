package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Entrega;
import com.transporte_gomez.erp.dto.EntregaFiltro;
import com.transporte_gomez.erp.dto.Reporte;
import com.transporte_gomez.erp.services.EntregaServices;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/entregas")
public class EntregaController {

    private final EntregaServices entregaServices;

    @GetMapping
    public Page<Entrega> findAll(Pageable pageable, EntregaFiltro filtro) {
        return entregaServices.getEntregas(pageable, filtro);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        entregaServices.deleteEntrega(id);
    }

    @GetMapping("/reporte/mes")
    public List<Reporte> obtenerEntregasPorMes(EntregaFiltro filtro) {
        return entregaServices.obtenerEntregasEntregadasPorMes(filtro);
    }

    @GetMapping("/reporte/top-escuelas")
    public List<Reporte> findTop10EscuelasConMasEntregas(EntregaFiltro filtro) {
        return entregaServices.findTopEscuelasConMasEntregas(filtro);
    }

    @PutMapping(value = "/{id}/recepcionado", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void entregar(@PathVariable Integer id, @RequestPart(value = "files", required = false) List<MultipartFile> files){
        entregaServices.entregar(id, files);
    }
}
