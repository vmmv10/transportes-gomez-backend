package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.UsuarioAdmin;
import com.transporte_gomez.erp.services.UsuarioAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Administración de usuarios (solo rol Administrador: ver SecurityConfig). */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioAdminService usuarioAdminService;

    @GetMapping
    public List<UsuarioAdmin> listar() {
        return usuarioAdminService.listar();
    }

    @GetMapping("/roles")
    public List<String> roles() {
        return usuarioAdminService.roles();
    }

    /** Crea el usuario en Auth0 y le envía el correo para definir su contraseña. */
    @PostMapping
    public UsuarioAdmin crear(@RequestBody UsuarioAdmin usuario) {
        return usuarioAdminService.crear(usuario);
    }

    @PutMapping("/{id}")
    public UsuarioAdmin actualizar(@PathVariable Long id, @RequestBody UsuarioAdmin usuario) {
        return usuarioAdminService.actualizar(id, usuario);
    }

    @PutMapping("/{id}/bloquear")
    public void bloquear(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        usuarioAdminService.bloquear(id, true, jwt.getSubject());
    }

    @PutMapping("/{id}/desbloquear")
    public void desbloquear(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        usuarioAdminService.bloquear(id, false, jwt.getSubject());
    }

    @PostMapping("/{id}/correo-clave")
    public void enviarCorreoClave(@PathVariable Long id) {
        usuarioAdminService.enviarCorreoClave(id);
    }
}
