package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.*;
import com.transporte_gomez.erp.services.EntregaServices;
import com.transporte_gomez.erp.services.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/entregas")
public class EntregaController {

    private final EntregaServices entregaServices;
    private final UsuarioService usuarioService;

    @GetMapping
    public Page<Entrega> findAll(Pageable pageable, EntregaFiltro filtro, @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = usuarioService.obtenerUsuarioLogeado(jwt);
        if (usuario.getRol() != null && usuario.getRol().equalsIgnoreCase("repartidor")) {
            filtro.setChofer(usuario.getId());
        }
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

    @GetMapping("/reporte/entregas-vs-no-entregadas")
    public List<Reporte> obtenerEntregasEntregadasVsNoEntregadas(EntregaFiltro filtro) {
        return entregaServices.obtenerEntregasEntregadasVsNoEntregadas(filtro);
    }

    @GetMapping("/reporte/ultimas-entregas")
    public List<Reporte> obtenerUltimasEntregas(EntregaFiltro filtro) {
        return entregaServices.obtenerUltimasEntregas(filtro);
    }

    @GetMapping("/reporte/promedio-diario")
    public Double obtenerPromedioDiario(EntregaFiltro filtro) {
        return entregaServices.obtenerPromedioDiario(filtro);
    }

    @GetMapping("/reporte/entregas-por-dia/count")
    public Long countEntregasParaHoyPorEscuela(EntregaFiltro filtro) {
        return entregaServices.countEntregasParaHoyPorEscuela(filtro);
    }

    @GetMapping("/reporte/escuelas-con-pendientes")
    public List<Reporte> obtenerEscuelasConPendientes(EntregaFiltro filtro) {
        return entregaServices.obtenerEscuelasConPendientes(filtro);
    }

    @GetMapping("/reporte/stats")
    public EntregaDashboard getStats(EntregaFiltro filtro) {
        return entregaServices.getStats(filtro);
    }

    @PostMapping("/excel")
    public ResponseEntity<InputStreamResource> exportarExcel(EntregaFiltro filtro) {

        ByteArrayInputStream in = entregaServices.exportarEntregasExcel(filtro);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=entregas.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/kpi")
    public EntregaKpi obtenerKpi(EntregaKpiFiltro filtro) {
        return entregaServices.obtenerKpi(filtro);
    }

}
