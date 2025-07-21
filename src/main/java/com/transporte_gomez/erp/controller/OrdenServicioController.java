package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.OrdenServicio;
import com.transporte_gomez.erp.dto.OrdenServicioFiltro;
import com.transporte_gomez.erp.dto.Usuario;
import com.transporte_gomez.erp.services.OrdenServicioService;
import com.transporte_gomez.erp.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/ordenes-servicios")
public class OrdenServicioController {

    private final OrdenServicioService ordenServicioService;
    private final UsuarioService usuarioService;

    @GetMapping()
    public Page<OrdenServicio> getOrdenServicios(Pageable pageable, OrdenServicioFiltro filtro) {
        return ordenServicioService.getOrdenServicios(pageable, filtro);
    }

    @GetMapping("/{id}")
    public OrdenServicio getOrdenServicio(@PathVariable Long id) {
        return ordenServicioService.getOrdenServicio(id);
    }

    @PostMapping()
    public OrdenServicio createOrdenServicio(@RequestBody OrdenServicio ordenServicio,
                                             @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = usuarioService.obtenerUsuarioLogeado(jwt);
        return ordenServicioService.createOrdenServicio(ordenServicio, usuario);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public OrdenServicio updateOrdenServicio(@PathVariable Long id,
                                             @RequestPart("orden") OrdenServicio ordenServicio,
                                             @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                             @AuthenticationPrincipal Jwt jwt) {
        Usuario usuario = usuarioService.obtenerUsuarioLogeado(jwt);
        return ordenServicioService.updateOrdenServicio(id, ordenServicio, files, usuario);
    }

    @DeleteMapping("/{id}")
    public void deleteOrdenServicio(@PathVariable Long id) {
        ordenServicioService.deleteOrdenServicio(id);
    }

    @PostMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generarPdf(@PathVariable Long id) {
        return ordenServicioService.generarPdf(id);
    }

    @DeleteMapping("/detalleS/{detalleId}")
    public void deleteDetalle(@PathVariable Long detalleId) {
        ordenServicioService.deleteDetalle(detalleId);
    }
}
