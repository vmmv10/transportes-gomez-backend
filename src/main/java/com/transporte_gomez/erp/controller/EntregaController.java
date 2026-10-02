package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.config.AlcanceConductor;
import com.transporte_gomez.erp.dto.*;
import com.transporte_gomez.erp.services.EntregaServices;
import com.transporte_gomez.erp.services.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/entregas")
public class EntregaController {

    private final EntregaServices entregaServices;
    private final UsuarioService usuarioService;
    private final AlcanceConductor alcanceConductor;

    @GetMapping
    public Page<Entrega> findAll(Pageable pageable, EntregaFiltro filtro, @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = usuarioService.obtenerUsuarioLogeado(jwt);
        if (alcanceConductor.soloConductor() || (usuario.getRol() != null && usuario.getRol().equalsIgnoreCase("repartidor"))) {
            filtro.setChofer(usuario.getId());
        }
        return entregaServices.getEntregas(pageable, filtro);
    }

    @RequestMapping(value = "/excel", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<byte[]> descargarExcel(EntregaFiltro filtro, Sort sort, @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = usuarioService.obtenerUsuarioLogeado(jwt);
        if (alcanceConductor.soloConductor() || (usuario.getRol() != null && usuario.getRol().equalsIgnoreCase("repartidor"))) {
            filtro.setChofer(usuario.getId());
        }

        byte[] excel = entregaServices.generarExcel(filtro, sort);
        String nombreArchivo = "Entregas_" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(nombreArchivo, StandardCharsets.UTF_8).build().toString())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(excel.length)
                .body(excel);
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

    /** latitud/longitud opcionales: dónde se registró la entrega (app móvil). */
    @PutMapping(value = "/{id}/recepcionado", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void entregar(@PathVariable Integer id, @RequestPart(value = "files", required = false) List<MultipartFile> files,
                         @RequestParam(required = false) BigDecimal latitud, @RequestParam(required = false) BigDecimal longitud) {
        alcanceConductor.verificarEntrega(id);
        alcanceConductor.verificarRutaComenzada(id);
        entregaServices.entregar(id, files, latitud, longitud);
    }

    /** {"estado": "NO_ENTREGADO" | "RECHAZADO", "motivo": "Establecimiento cerrado", "latitud": .., "longitud": ..} */
    @PostMapping("/{id}/no-entregado")
    public void noEntregado(@PathVariable Integer id, @RequestBody EntregaResultado resultado) {
        alcanceConductor.verificarEntrega(id);
        alcanceConductor.verificarRutaComenzada(id);
        entregaServices.noEntregado(id, resultado);
    }

    /** La app avisa que el conductor está cerca del destino. */
    @PostMapping("/{id}/proximidad")
    public void proximidad(@PathVariable Integer id, @RequestBody EntregaResultado resultado) {
        alcanceConductor.verificarEntrega(id);
        entregaServices.proximidad(id, resultado);
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

    @GetMapping("/reporte/kpis")
    public List<Kpi> getKpis(EntregaFiltro filtro) {
        return entregaServices.getKpis(filtro);
    }

}
