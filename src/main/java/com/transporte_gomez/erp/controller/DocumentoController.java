package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Documento;
import com.transporte_gomez.erp.dto.DocumentoFiltro;
import com.transporte_gomez.erp.dto.Usuario;
import com.transporte_gomez.erp.services.DocumentoService;
import com.transporte_gomez.erp.services.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;
    private final UsuarioService usuarioService;

    @GetMapping()
    public Page<Documento> getDocumentos(Pageable pageable, DocumentoFiltro documentoFiltro) {
        return documentoService.findAll(pageable, documentoFiltro);
    }

    @GetMapping("/{id}")
    public Documento getDocumentoById(@PathVariable Long id) {
        return documentoService.getById(id);
    }

    @GetMapping("/{numero}/{tipo}")
    public Documento getDocumentoByNumero(@PathVariable Long numero, @PathVariable Integer tipo) {
        return documentoService.getByNumero(numero, tipo);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Documento createDocumento(
            @RequestPart("documento") Documento documento,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal Jwt jwt) {

        Usuario usuario = usuarioService.obtenerUsuarioLogeado(jwt);
        documento.setUsuario(usuario);

        return documentoService.createDocumento(documento, files);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Documento updateDocumento(
            @PathVariable Long id,
            @RequestPart("documento") Documento documento,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal Jwt jwt) {

        Usuario usuario = usuarioService.obtenerUsuarioLogeado(jwt);
        documento.setUsuario(usuario);

        return documentoService.updateDocumento(id, documento, files);
    }

    @DeleteMapping("/{id}")
    public void deleteDocumento(@PathVariable Long id) {
        documentoService.deleteDocumento(id);
    }
}
