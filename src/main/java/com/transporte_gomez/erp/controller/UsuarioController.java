package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Usuario;
import com.transporte_gomez.erp.services.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/usuario")
    public String getUsuario(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        return "Usuario logueado: " + email;
    }

    @GetMapping
    public List<Usuario> listarUsuarios(Usuario filtro) {
        return usuarioService.getAll(filtro);
    }

}
