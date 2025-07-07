package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Usuario;
import com.transporte_gomez.erp.services.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/email")
    public String getUsuarioEmail(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        return "Usuario logueado: " + email;
    }

    @GetMapping("/autenticado")
    public Usuario getUsuario(@AuthenticationPrincipal Jwt jwt) {
        return usuarioService.obtenerUsuarioLogeado(jwt);
    }

    @GetMapping
    public List<Usuario> listarUsuarios(Usuario filtro) {
        return usuarioService.getAll(filtro);
    }

    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        usuario.setId(id);
        return usuarioService.UpdateUsuario(usuario);
    }

}
